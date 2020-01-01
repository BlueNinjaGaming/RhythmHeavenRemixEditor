package io.github.chrislo27.rhre3.screen

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.Screen
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Align
import io.github.chrislo27.rhre3.RHRE3
import io.github.chrislo27.rhre3.RHRE3Application
import io.github.chrislo27.toolboks.Toolboks
import io.github.chrislo27.toolboks.ToolboksScreen
import io.github.chrislo27.toolboks.logging.SysOutPiper
import io.github.chrislo27.toolboks.ui.ImageLabel
import io.github.chrislo27.toolboks.ui.Stage
import io.github.chrislo27.toolboks.ui.TextLabel
import io.github.chrislo27.toolboks.ui.UIPalette
import java.io.PrintWriter
import java.io.StringWriter


class CrashScreen(main: RHRE3Application, val throwable: Throwable, val lastScreen: Screen?)
    : ToolboksScreen<RHRE3Application, CrashScreen>(main), HidesVersionText {

    private data class Splash(val title: String, val subtitle: String, val titleScale: Float = 1f)

    companion object {
        private val splashes: List<Splash> = listOf(
                Splash("S-crash-o, hey!", "I don't think you wanted the program to break (c'mon, ooh)."),
                Splash("I'm a broken man...", "...I'm just a shattering storm..."),
                Splash("Martian: \uE06B\uE06B\uE06B\uE06B  \uE06B\uE06B", "Translator Tom: RHRE has crashed.", 0.85f),
                Splash("AAAAAAAAAAAAAAAAAA", "Together now!")
                                                   )
    }

    override val stage: Stage<CrashScreen> = Stage(null, main.defaultCamera, 1280f, 720f)

    private var crashIcon: Texture? = null

    init {
        val palette = main.uiPalette

        fun label(pal: UIPalette = palette, st: Stage<CrashScreen> = stage): TextLabel<CrashScreen> = TextLabel(pal, st, st).apply {
            this.isLocalizationKey = false
            this.textWrapping = false
        }

        val selectedSplash = splashes.random()

        stage.elements += label(palette.copy(ftfont = main.defaultFontLargeFTF)).apply {
            this.text = selectedSplash.title
            this.textAlign = Align.left
            this.fontScaleMultiplier = selectedSplash.titleScale
            this.location.set(screenX = 0.2f, screenWidth = 1f /*0.75f*/, screenY = 0.8f, screenHeight = 0.2f)
        }
        try {
            val icon = Texture("images/icon/crash_icon.png")
            crashIcon = icon
            stage.elements += ImageLabel(palette, stage, stage).apply {
                this.image = TextureRegion(icon)
                this.renderType = ImageLabel.ImageRendering.ASPECT_RATIO
                this.location.set(screenX = 0.1f, screenY = 0.9f, screenWidth = 0f, screenHeight = 0f,
                                  pixelWidth = 128f, pixelHeight = 128f, pixelX = -64f, pixelY = -64f)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toolboks.LOGGER.warn("Failed to load crash screen icon")
        }

        stage.elements += label().apply {
            this.location.set(screenY = 0.5f, screenHeight = 0.3f)
            this.fontScaleMultiplier = 0.9f
            this.text = "${selectedSplash.subtitle}\n\nThe program has crashed, but we're able to display this crash info screen.\nWe've attempted to save your remix (if any) and you should be able to recover it the next time\nyou start the program. " + (if (!RHRE3.noAnalytics) "An anonymous crash report has also been sent to the developer." else "") + "\nIf you can, take a screenshot of this screen as it contains useful info for the developer.\nConsider submitting a bug report at\n[#8CCFFF]${RHRE3.GITHUB}/issues/new/choose[]."
        }

        stage.elements += label().apply {
            this.location.set(screenY = 0.05f, screenHeight = 0.45f, screenX = 0.1f, screenWidth = 0.875f)
            this.fontScaleMultiplier = 0.85f
            this.textAlign = Align.topLeft
            this.textWrapping = true
            this.text = "Last screen: ${lastScreen?.javaClass?.canonicalName}\nLog file: ${if (RHRE3.portableMode) "./.rhre3/logs/" else "~/.rhre3/logs"}/${SysOutPiper.logFile.name}\nException: [#FF6B68]${StringWriter().apply {
                val pw = PrintWriter(this)
                throwable.printStackTrace(pw)
                pw.flush()
            }.toString().replace("\t", "    ")}[]"
        }

        stage.elements += label().apply {
            this.location.set(screenWidth = 0.5f, screenHeight = 0.03333f, pixelHeight = 8f, pixelWidth = -6f)
            this.textAlign = Align.left
            this.fontScaleMultiplier = 0.75f
            this.text = "When you're done, you can close the program."
            this.background = true
        }

        stage.elements += label().apply {
            this.location.set(screenX = 0.5f, screenWidth = 0.5f, screenHeight = 0.03333f, pixelX = 6f, pixelWidth = -6f, pixelHeight = 8f)
            this.textAlign = Align.right
            this.fontScaleMultiplier = 0.75f
            this.text = RHRE3.VERSION.toString()
            this.background = true
        }
    }

    override fun render(delta: Float) {
        if (main.batch.isDrawing) {
            main.batch.end()
        }
        super.render(delta)
    }

    override fun renderUpdate() {
        super.renderUpdate()
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
//            main.screen = ScreenRegistry["editor"]
        }
    }

    override fun tickUpdate() {
    }

    override fun dispose() {
        crashIcon?.dispose()
    }

}
