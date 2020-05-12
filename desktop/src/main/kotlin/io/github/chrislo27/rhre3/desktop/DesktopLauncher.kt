package io.github.chrislo27.rhre3.desktop

import com.badlogic.gdx.Files
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.glutils.HdpiMode
import com.beust.jcommander.JCommander
import com.beust.jcommander.ParameterException
import io.github.chrislo27.rhre3.RHRE3
import io.github.chrislo27.rhre3.RHRE3Application
import io.github.chrislo27.toolboks.desktop.ToolboksDesktopLauncher3
import io.github.chrislo27.toolboks.lazysound.LazySound
import io.github.chrislo27.toolboks.logging.Logger
import java.io.File

object DesktopLauncher {
    
    private fun printHelp(jCommander: JCommander) {
        println("${RHRE3.TITLE} ${RHRE3.VERSION}\n${RHRE3.GITHUB}\n\n${StringBuilder().apply { jCommander.usage(this) }}")
    }
    
    @JvmStatic
    fun main(args: Array<String>) {
        // https://github.com/chrislo27/RhythmHeavenRemixEditor/issues/273
        System.setProperty("jna.nosys", "true")
        
        RHRE3.launchArguments = args.toList()
        
        try {
            // Check for bad arguments but don't cause a full crash
            JCommander.newBuilder().acceptUnknownOptions(false).addObject(Arguments()).build().parse(*args)
        } catch (e: ParameterException) {
            println("WARNING: Failed to parse arguments. Check below for details and help documentation. You may have strange parse results from ignoring unknown options.\n")
            e.printStackTrace()
            println("\n\n")
            printHelp(JCommander(Arguments()))
            println("\n\n")
        }
        
        val arguments = Arguments()
        val jcommander = JCommander.newBuilder().acceptUnknownOptions(true).addObject(arguments).build()
        jcommander.parse(*args)
        
        if (arguments.printHelp) {
            printHelp(jcommander)
            return
        }
        
        val logger = Logger()
        val portable = arguments.portableMode
        val app = RHRE3Application(logger, File(if (portable) ".rhre3/logs/" else System.getProperty("user.home") + "/.rhre3/logs/"))
        ToolboksDesktopLauncher3(app)
                .editConfig {
                    this.setAutoIconify(true)
                    this.setWindowedMode(app.emulatedSize.first, app.emulatedSize.second)
                    this.setWindowSizeLimits(640, 360, -1, -1)
                    this.setTitle(app.getTitle())
                    this.setIdleFPS(arguments.fps.coerceAtLeast(30))
                    this.setResizable(true)
                    this.useVsync(arguments.fps <= 60)
                    this.setInitialBackgroundColor(Color(0f, 0f, 0f, 1f))
                    this.setAudioConfig(100, 16384, 32)
                    this.setHdpiMode(HdpiMode.Logical)
                    if (portable) {
                        this.setPreferencesConfig(".rhre3/.prefs/", Files.FileType.Local)
                    }
                    
                    RHRE3.portableMode = portable
                    RHRE3.skipGitScreen = arguments.skipGit
                    RHRE3.forceGitFetch = arguments.forceGitFetch
                    RHRE3.forceGitCheck = arguments.forceGitCheck
                    RHRE3.verifySfxDb = arguments.verifySfxdb
                    RHRE3.immediateEvent = when {
                        arguments.eventImmediateAnniversaryLikeNew -> 2
                        arguments.eventImmediateAnniversary -> 1
                        arguments.eventImmediateXmas -> 3
                        else -> 0
                    }
                    RHRE3.noAnalytics = arguments.noAnalytics
                    RHRE3.noOnlineCounter = arguments.noOnlineCounter
                    RHRE3.outputGeneratedDatamodels = arguments.outputGeneratedDatamodels
                    RHRE3.outputCustomSfx = arguments.outputCustomSfx
                    RHRE3.showTapalongMarkersByDefault = arguments.showTapalongMarkers
                    RHRE3.midiRecording = arguments.midiRecording
                    RHRE3.logMissingLocalizations = arguments.logMissingLocalizations
                    RHRE3.disableCustomSounds = arguments.disableCustomSounds
                    RHRE3.lc = arguments.lc
                    RHRE3.triggerUpdateScreen = arguments.triggerUpdateScreen
                    LazySound.loadLazilyWithAssetManager = !arguments.lazySoundsForceLoad
                    
                    this.setWindowIcon()
                    val sizes: List<Int> = listOf(256, 128, 64, 32, 24, 16)
                    this.setWindowIcon(Files.FileType.Internal, *sizes.map { "images/icon/$it.png" }.toTypedArray())
                }
                .launch()
    }
    
}
