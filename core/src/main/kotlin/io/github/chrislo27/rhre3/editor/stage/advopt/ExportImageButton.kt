package io.github.chrislo27.rhre3.editor.stage.advopt

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.graphics.Pixmap
import com.badlogic.gdx.graphics.PixmapIO
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.glutils.FrameBuffer
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.GdxRuntimeException
import com.badlogic.gdx.utils.ScreenUtils
import io.github.chrislo27.rhre3.PreferenceKeys
import io.github.chrislo27.rhre3.RHRE3
import io.github.chrislo27.rhre3.editor.Editor
import io.github.chrislo27.rhre3.entity.model.ModelEntity
import io.github.chrislo27.rhre3.screen.EditorScreen
import io.github.chrislo27.rhre3.util.*
import io.github.chrislo27.toolboks.Toolboks
import io.github.chrislo27.toolboks.ui.*
import io.github.chrislo27.toolboks.util.gdxutils.isShiftDown
import java.io.IOException
import kotlin.math.roundToInt
import kotlin.math.sqrt


class ExportImageButton(val editor: Editor, palette: UIPalette, parent: UIElement<EditorScreen>,
                        stage: Stage<EditorScreen>)
    : Button<EditorScreen>(palette, parent, stage) {

    init {
        addLabel(TextLabel(palette, this, this.stage).apply {
            this.text = "Export\nImage"
            this.textWrapping = false
            this.isLocalizationKey = false
            this.fontScaleMultiplier = 0.35f
        })
        this.visible = false
    }

    override fun render(screen: EditorScreen, batch: SpriteBatch, shapeRenderer: ShapeRenderer) {
        this.enabled = editor.remix.duration.isFinite()
        super.render(screen, batch, shapeRenderer)
    }

    override var tooltipText: String?
        set(_) {}
        get() {
            return "Export remix as image\nHold [CYAN]SHIFT[] and click to export a horizontal image"
        }

    override fun onLeftClick(xPercent: Float, yPercent: Float) {
        super.onLeftClick(xPercent, yPercent)
        val wasShiftHeld = Gdx.input.isShiftDown()
        val filters = listOf(FileChooserExtensionFilter("Supported image output files", "*.png"))
        FileChooser.saveFileChooser("Choose image to export to", attemptRememberDirectory(editor.main, PreferenceKeys.FILE_CHOOSER_EXPORT_IMAGE) ?: getDefaultDirectory(), null, filters) { file ->
            val remix = editor.remix
            val duration = remix.duration
            if (file != null && duration > 0f) {
                val newInitialDirectory = if (!file.isDirectory) file.parentFile else file
                persistDirectory(editor.main, PreferenceKeys.FILE_CHOOSER_EXPORT_IMAGE, newInitialDirectory)
                Gdx.app.postRunnable {
                    val scale = 1f // Very large scales may cause errors when initing the pixmap
                    val singleRowWidth = (duration + 1) * Editor.ENTITY_WIDTH * scale
                    val singleRowHeight = ((remix.trackCount + 4) * Editor.ENTITY_HEIGHT) * scale
                    val rowEstimate = (sqrt(singleRowWidth / singleRowHeight).roundToInt()).coerceAtLeast(1)
                    val rows = if (wasShiftHeld) 1 else rowEstimate
                    val pixmap = Pixmap((singleRowWidth / rows).roundToInt(), singleRowHeight.roundToInt() * rows, Pixmap.Format.RGBA8888)
                    pixmap.blending = Pixmap.Blending.None

                    val oldCamX = editor.camera.position.x
                    val oldCamZoom = editor.camera.zoom
                    val buffer = FrameBuffer(Pixmap.Format.RGB888, (RHRE3.WIDTH * scale).roundToInt(), (RHRE3.HEIGHT * scale).roundToInt(), false, true)
                    val wasTextOnScreen = ModelEntity.attemptTextOnScreen
                    ModelEntity.attemptTextOnScreen = false

                    buffer.begin()

                    Toolboks.LOGGER.debug("Pixmap size: ${pixmap.width} by ${pixmap.height} with $rows rows")

                    val stepValue = (editor.camera.viewportWidth * Editor.ENTITY_WIDTH * scale).roundToInt()
                    for (x in 0..(pixmap.width * rows) step stepValue) {
                        editor.camera.position.x = x / (Editor.ENTITY_WIDTH * scale) + editor.camera.viewportWidth / 2f - 0.5f
                        editor.camera.zoom = 1f
                        editor.camera.update()

                        editor.render(updateDelta = false, otherUI = false, noGlassEffect = true, disableThemeUsesMenu = true)

                        val bufPix = ScreenUtils.getFrameBufferPixmap(0, 0, buffer.width, buffer.height)
                        val currentRow = x / (pixmap.width)
                        pixmap.drawPixmap(bufPix, x % (pixmap.width), (rows - 1 - currentRow) * (pixmap.height / rows), 0, (Editor.ENTITY_HEIGHT * 4.5f * scale).roundToInt(), bufPix.width, pixmap.height / rows)
                        // if the end of this section spans into the next row, render that section too
                        val endRow = (x + stepValue) / (pixmap.width)
                        if (endRow > currentRow) {
                            Toolboks.LOGGER.debug("Stepping into next row: $currentRow -> $endRow")
                            pixmap.drawPixmap(bufPix, (x + stepValue) % (pixmap.width) - stepValue, (rows - 1 - endRow) * (pixmap.height / rows), 0, (Editor.ENTITY_HEIGHT * 4.5f * scale).roundToInt(), bufPix.width, pixmap.height / rows)
                        }
                        bufPix.dispose()

                        Toolboks.LOGGER.debug("Copying to pixmap: ${x / (pixmap.width.toFloat() * rows) * 100}%")
                    }

                    editor.camera.position.x = oldCamX
                    editor.camera.zoom = oldCamZoom
                    editor.camera.update()

                    buffer.end()

                    Toolboks.LOGGER.debug("Writing to file...")
                    try {
                        val writer = PixmapIO.PNG((pixmap.width.toFloat() * pixmap.height.toFloat() * 1.5f).toInt()) // Guess at deflated size.
                        try {
                            writer.setFlipY(true)
                            writer.write(FileHandle(file), pixmap)
                        } finally {
                            writer.dispose()
                        }
                    } catch (ex: IOException) {
                        throw GdxRuntimeException("Error writing PNG: $file", ex)
                    }
                    Toolboks.LOGGER.debug("Done.")
                    pixmap.dispose()
                    buffer.dispose()
                    ModelEntity.attemptTextOnScreen = wasTextOnScreen
                }
            }
        }
    }
}