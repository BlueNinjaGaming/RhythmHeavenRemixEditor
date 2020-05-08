package io.github.chrislo27.rhre3.screen

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Align
import io.github.chrislo27.rhre3.RHRE3Application
import io.github.chrislo27.rhre3.RemixRecovery
import io.github.chrislo27.rhre3.stage.GenericStage
import io.github.chrislo27.toolboks.ToolboksScreen
import io.github.chrislo27.toolboks.i18n.Localization
import io.github.chrislo27.toolboks.registry.AssetRegistry
import io.github.chrislo27.toolboks.registry.ScreenRegistry
import io.github.chrislo27.toolboks.ui.Button
import io.github.chrislo27.toolboks.ui.ImageLabel
import io.github.chrislo27.toolboks.ui.Stage
import io.github.chrislo27.toolboks.ui.TextLabel
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle


class RecoverRemixScreen(main: RHRE3Application)
    : ToolboksScreen<RHRE3Application, RecoverRemixScreen>(main) {

    override val stage: Stage<RecoverRemixScreen> = GenericStage(main.uiPalette, null, main.defaultCamera)

    private val label: TextLabel<RecoverRemixScreen>

    init {
        val palette = main.uiPalette
        stage as GenericStage
        stage.titleIcon.image = TextureRegion(AssetRegistry.get<Texture>("ui_icon_folder"))
        stage.titleLabel.text = "screen.recovery.title"
        stage.backButton.visible = true
        stage.backButton.palette = palette.copy(highlightedBackColor = Color(1f, 0f, 0f, 0.5f),
                                                clickedBackColor = Color(1f, 0.5f, 0.5f, 0.5f))
        stage.onBackButtonClick = {
            main.screen = ScreenRegistry.getNonNull("editor")
        }

        label = TextLabel(palette, stage.centreStage, stage.centreStage).apply {
            this.location.set(0f, 0f, 1f, 0.6f)
            this.textAlign = Align.center
            this.isLocalizationKey = false
        }
        stage.centreStage.elements += label
        stage.centreStage.elements += TextLabel(palette, stage.centreStage, stage.centreStage).apply {
            this.location.set(0.3f, 0.6f, 0.6f, 0.4f)
            this.textAlign = Align.left
            this.isLocalizationKey = true
            this.text = "screen.recovery.onlyChance"
        }
        stage.centreStage.elements += ImageLabel(palette, stage.centreStage, stage.centreStage).apply {
            this.image = TextureRegion(AssetRegistry.get<Texture>("ui_icon_warn"))
            this.renderType = ImageLabel.ImageRendering.ASPECT_RATIO
            this.location.set(0.1f, 0.6f, 0.2f, 0.4f)
        }

        stage.bottomStage.elements += Button(palette.copy(highlightedBackColor = Color(0f, 1f, 0f, 0.5f), clickedBackColor = Color(0.5f, 1f, 0.5f, 0.5f)), stage.bottomStage, stage.bottomStage).apply {
            this.location.set(screenX = 0.2f, screenWidth = 0.6f)
            addLabel(TextLabel(palette, this, this.stage).apply {
                this.text = "screen.recovery.button"
                this.isLocalizationKey = true
            })
            this.leftClickAction = { _, _ ->
                val screen = ScreenRegistry.getNonNullAsType<OpenRemixScreen>("openRemix")
                screen.loadFile(RemixRecovery.recoveryFile.file(), overrideAutosave = true)
                main.screen = ScreenRegistry["editor"] // This forces the last checksum to be of a blank remix
                main.screen = screen
            }
        }
    }

    override fun show() {
        super.show()
        val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG, FormatStyle.SHORT)
                .withLocale(Localization.currentBundle.locale.locale)
        label.text = Localization["screen.recovery.label", formatter.format(RemixRecovery.getLastLocalDateTime())]
    }

    override fun tickUpdate() {
    }

    override fun dispose() {
    }

}