package io.github.chrislo27.rhre3.entity.model.special

import com.badlogic.gdx.graphics.Color
import io.github.chrislo27.rhre3.editor.Editor
import io.github.chrislo27.rhre3.entity.model.IRepitchable
import io.github.chrislo27.rhre3.entity.model.IStretchable
import io.github.chrislo27.rhre3.entity.model.ModelEntity
import io.github.chrislo27.rhre3.sfxdb.datamodel.impl.special.ShakeScreen
import io.github.chrislo27.rhre3.theme.Theme
import io.github.chrislo27.rhre3.track.Remix
import io.github.chrislo27.rhre3.util.Semitones
import kotlin.math.roundToLong


class ShakeEntity(remix: Remix, datamodel: ShakeScreen)
    : ModelEntity<ShakeScreen>(remix, datamodel), IStretchable, IRepitchable {

    companion object {

        fun getShakeIntensity(semitone: Int): Float {
            return Semitones.getALPitch(semitone)
        }

    }

    override val isStretchable: Boolean = true
    override var semitone: Int = 0
    override val canBeRepitched: Boolean = true
    override val showPitchOnTooltip: Boolean = false

    init {
        bounds.height = 1f
    }

    override fun getRenderColor(editor: Editor, theme: Theme): Color {
        return theme.entities.special
    }

    override fun getTextForSemitone(semitone: Int): String {
        return "${(getShakeIntensity(semitone) * 100).roundToLong()}%"
    }

    override fun onStart() {
        if (this !in remix.currentShakeEntities) {
            remix.currentShakeEntities += this
        }
    }

    override fun whilePlaying() {
    }

    override fun onEnd() {
        remix.currentShakeEntities -= this
    }

    override fun copy(remix: Remix): ShakeEntity {
        return ShakeEntity(remix, datamodel).also {
            it.updateBounds {
                it.bounds.set(this@ShakeEntity.bounds)
            }
            it.semitone = this@ShakeEntity.semitone
        }
    }

}