package io.github.chrislo27.rhre3.sfxdb.datamodel.impl

import io.github.chrislo27.rhre3.entity.model.multipart.PatternEntity
import io.github.chrislo27.rhre3.sfxdb.Game
import io.github.chrislo27.rhre3.sfxdb.SFXDatabase
import io.github.chrislo27.rhre3.sfxdb.datamodel.ContainerModel
import io.github.chrislo27.rhre3.sfxdb.datamodel.Datamodel
import io.github.chrislo27.rhre3.sfxdb.datamodel.PreviewableModel
import io.github.chrislo27.rhre3.track.Remix

class Pattern(game: Game, id: String, deprecatedIDs: List<String>, name: String, subtext: String = "",
              override val cues: List<CuePointer>, val stretchable: Boolean)
    : Datamodel(game, id, deprecatedIDs, name, subtext), ContainerModel, PreviewableModel {

    override val canBePreviewed: Boolean by lazy { PreviewableModel.determineFromCuePointers(cues) }

    val repitchable: Boolean by lazy {
        cues.any {
            (SFXDatabase.data.objectMap[it.id] as? Cue)?.repitchable == true
        }
    }

    override val duration: Float by lazy {
        cues.map { it.beat + it.duration }.maxOrNull()!!
    }

    override fun createEntity(remix: Remix,
                              cuePointer: CuePointer?): PatternEntity {
        return PatternEntity(remix, this).apply {
            if (cuePointer != null) {
                semitone = cuePointer.semitone
                volumePercent = cuePointer.volume
            }
        }
    }

    override fun dispose() {
    }

}
