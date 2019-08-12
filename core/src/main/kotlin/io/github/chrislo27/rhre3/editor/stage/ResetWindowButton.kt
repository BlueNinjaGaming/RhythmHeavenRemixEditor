package io.github.chrislo27.rhre3.editor.stage

import io.github.chrislo27.rhre3.editor.Editor
import io.github.chrislo27.rhre3.screen.EditorScreen
import io.github.chrislo27.toolboks.i18n.Localization
import io.github.chrislo27.toolboks.ui.Button
import io.github.chrislo27.toolboks.ui.Stage
import io.github.chrislo27.toolboks.ui.UIElement
import io.github.chrislo27.toolboks.ui.UIPalette


class ResetWindowButton(val editor: Editor, palette: UIPalette, parent: UIElement<EditorScreen>,
                        stage: Stage<EditorScreen>)
    : Button<EditorScreen>(palette, parent, stage) {

    override var tooltipText: String?
        set(_) {}
        get() {
            return Localization["editor.resetwindow"] + " [LIGHT_GRAY](SHIFT+F11)[]"
        }

    override fun onLeftClick(xPercent: Float, yPercent: Float) {
        super.onLeftClick(xPercent, yPercent)
        editor.main.attemptResetWindow()
        editor.main.persistWindowSettings()
    }
}