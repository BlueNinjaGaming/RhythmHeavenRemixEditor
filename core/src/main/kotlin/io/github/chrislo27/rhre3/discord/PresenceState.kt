package io.github.chrislo27.rhre3.discord

import kotlin.math.roundToLong


sealed class PresenceState(open val state: String = "", open val smallIcon: String = "", open val smallIconText: String = state,
                           open val largeIcon: String? = null, open val largeIconText: String? = null) {

    open fun getPartyCount(): Pair<Int, Int> = DefaultRichPresence.DEFAULT_PARTY

    open fun modifyRichPresence(richPresence: DefaultRichPresence) {
    }

    // ---------------- IMPLEMENTATIONS BELOW ----------------

    object Loading
        : PresenceState("Loading...", "gear")

    object InEditor
        : PresenceState("In Editor")

    object Exporting
        : PresenceState("Exporting a remix", "export")

    object InSettings
        : PresenceState("In Info and Settings", "info")

    object ViewingCredits
        : PresenceState("Viewing the credits ❤", "credits")

    object ViewingCreditsTempoUp
        : PresenceState("Tempo Up Credits!", "credits")

    object ViewingNews
        : PresenceState("Reading the news", "news")

    object ViewingPartners
        : PresenceState("Viewing our partners", "credits")

    object PlayingAlong
        : PresenceState("Using Playalong Mode", "playalong", largeIcon = "playalong_logo")
    
    class PlayingEndlessGame(gameName: String)
        : PresenceState("Playing $gameName", "goat")

    sealed class Elapsable(state: String, val duration: Float, smallIcon: String = "", smallIconText: String = state)
        : PresenceState(state, smallIcon, smallIconText) {
        override fun modifyRichPresence(richPresence: DefaultRichPresence) {
            super.modifyRichPresence(richPresence)
            if (duration > 0f) {
                richPresence.endTimestamp = System.currentTimeMillis() / 1000L + duration.roundToLong()
            }
        }

        class PlayingMidi(duration: Float)
            : Elapsable("Playing a MIDI", duration)

    }

}
