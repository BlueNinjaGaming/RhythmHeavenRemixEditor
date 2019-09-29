package io.github.chrislo27.rhre3.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.utils.Align
import io.github.chrislo27.rhre3.RHRE3Application
import io.github.chrislo27.rhre3.RemixRecovery
import io.github.chrislo27.rhre3.editor.Editor
import io.github.chrislo27.rhre3.entity.model.ILoadsSounds
import io.github.chrislo27.rhre3.entity.model.ModelEntity
import io.github.chrislo27.rhre3.soundsystem.SoundCache
import io.github.chrislo27.rhre3.stage.GenericStage
import io.github.chrislo27.toolboks.ToolboksScreen
import io.github.chrislo27.toolboks.registry.AssetRegistry
import io.github.chrislo27.toolboks.registry.ScreenRegistry
import io.github.chrislo27.toolboks.ui.Button
import io.github.chrislo27.toolboks.ui.ImageLabel
import io.github.chrislo27.toolboks.ui.Stage
import io.github.chrislo27.toolboks.ui.TextLabel
import io.github.chrislo27.toolboks.util.gdxutils.maxX


class NewRemixScreen(main: RHRE3Application)
    : ToolboksScreen<RHRE3Application, NewRemixScreen>(main) {

    private val editorScreen: EditorScreen by lazy { ScreenRegistry.getNonNullAsType<EditorScreen>("editor") }
    private val editor: Editor
        get() = editorScreen.editor
    override val stage: Stage<NewRemixScreen> = GenericStage(main.uiPalette, null, main.defaultCamera)

    private val mainLabel: TextLabel<NewRemixScreen>

    init {
        stage as GenericStage
        stage.titleIcon.image = TextureRegion(AssetRegistry.get<Texture>("ui_icon_newremix"))
        stage.titleLabel.text = "screen.new.title"
        stage.backButton.visible = true
        stage.onBackButtonClick = {
            main.screen = ScreenRegistry.getNonNull("editor")
        }

        val palette = main.uiPalette

        stage.bottomStage.elements +=
                object : Button<NewRemixScreen>(palette.copy(highlightedBackColor = Color(1f, 0f, 0f, 0.5f),
                                                             clickedBackColor = Color(1f, 0.5f, 0.5f, 0.5f)),
                                                stage.bottomStage, stage.bottomStage) {
                    override fun onLeftClick(xPercent: Float, yPercent: Float) {
                        super.onLeftClick(xPercent, yPercent)
                        editor.remix.entities.forEach {
                            if (it is ILoadsSounds) {
                                it.unloadSounds()
                            }
                        }
                        SoundCache.unloadAll()
                        val oldRemix = editor.remix
                        val newRemix = editor.createRemix()
                        editor.remix = newRemix
                        oldRemix.entities.filter { it.bounds.x <= editor.camera.position.x + editor.camera.viewportWidth * 1.5f && it.bounds.maxX >= editor.camera.position.x - editor.camera.viewportWidth * 1.5f }
                                .filterIsInstance<ModelEntity<*>>()
                                .forEach { editor.explodeEntity(it, doExplode = true) }
                        val tmpRect = Rectangle(0f, 0f, 0f, 0.75f)
                        oldRemix.trackersReverseView.forEach { container ->
                            tmpRect.y = -0.75f * (container.renderLayer + 1)
                            container.map.values
                                    .filter { it.beat <= editor.camera.position.x + editor.camera.viewportWidth * 1.5f && it.endBeat >= editor.camera.position.x - editor.camera.viewportWidth * 1.5f }
                                    .forEach { tracker ->
                                        if (tracker.isZeroWidth) {
                                            tmpRect.width = 0.15f
                                            tmpRect.x = tracker.beat - tmpRect.width / 2
                                        } else {
                                            tmpRect.width = tracker.width
                                            tmpRect.x = tracker.beat
                                        }
                                        editor.explodeRegion(tmpRect, tracker.getColour(editor.theme))
                                    }
                        }
                        val timeSigColor = editor.theme.trackLine.cpy().apply { a *= 0.25f }
                        oldRemix.timeSignatures.map.values.forEach { ts ->
                            editor.explodeRegion(tmpRect.set(ts.beat + 0.125f, 0f, 0.25f, oldRemix.trackCount.toFloat()), timeSigColor)
                        }
                        this@NewRemixScreen.stage.onBackButtonClick()
                        RemixRecovery.cacheRemixChecksum(newRemix)
                        Gdx.app.postRunnable {
                            System.gc()
                        }
                    }
                }.apply {
                    this.location.set(screenX = 0.25f, screenWidth = 0.5f)
                    this.addLabel(TextLabel(palette, this, this.stage).apply {
                        this.textAlign = Align.center
                        this.text = "screen.new.button"
                        this.isLocalizationKey = true
                    })
                }
        mainLabel = object : TextLabel<NewRemixScreen>(palette, stage.centreStage, stage.centreStage) {
        }.apply {
            this.location.set(screenX = 0.5f, screenWidth = 0.5f - 0.125f)
            this.textAlign = Align.left
            this.isLocalizationKey = true
            this.text = "screen.new.warning"
        }
        stage.centreStage.elements += mainLabel
        val warn = ImageLabel(palette, stage.centreStage, stage.centreStage).apply {
            this.image = TextureRegion(AssetRegistry.get<Texture>("ui_icon_warn"))
            this.renderType = ImageLabel.ImageRendering.ASPECT_RATIO
            this.location.set(screenY = 0.125f, screenHeight = 0.75f)
        }
        stage.centreStage.elements += warn
        warn.apply {
            stage.updatePositions()
            this.location.set(screenWidth = stage.percentageOfWidth(this.location.realHeight))
            this.location.set(screenX = 0.5f - this.location.screenWidth)
        }

        stage.updatePositions()
    }

    override fun renderUpdate() {
        super.renderUpdate()

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            (stage as GenericStage).onBackButtonClick()
        }
    }

    override fun dispose() {
    }

    override fun tickUpdate() {
    }
}