package io.github.chrislo27.rhre3.sfxdb.datamodel.impl.special

import io.github.chrislo27.rhre3.entity.model.special.PlayalongEntity
import io.github.chrislo27.rhre3.playalong.PlayalongInput
import io.github.chrislo27.rhre3.playalong.PlayalongMethod
import io.github.chrislo27.rhre3.sfxdb.Game
import io.github.chrislo27.rhre3.sfxdb.datamodel.PickerName
import io.github.chrislo27.rhre3.sfxdb.datamodel.impl.CuePointer
import io.github.chrislo27.rhre3.track.Remix


class PlayalongModel(game: Game, id: String, deprecatedIDs: List<String>, name: String,
                     val stretchable: Boolean,
                     val playalongInput: PlayalongInput,
                     val playalongMethod: PlayalongMethod,
                     override val pickerName: PickerName = PickerName(name, ""))
    : SpecialDatamodel(game, id, deprecatedIDs, "Playalong - $name", if (playalongMethod.instantaneous) 0.5f else 1f) {

    override val hideInPresentationMode: Boolean = true
    
    override fun createEntity(remix: Remix, cuePointer: CuePointer?): PlayalongEntity {
        return PlayalongEntity(remix, this)
    }

    override fun dispose() {
    }
}