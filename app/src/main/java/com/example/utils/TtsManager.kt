package com.example.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.ui.components.RobotTab
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false
    private var isMuted = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale.US)
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TtsManager", "US English Language not supported for TTS")
            } else {
                isInitialized = true
                tts?.setPitch(1.55f) // Cute high-pitched pet voice (like EMO / Vector)
                tts?.setSpeechRate(1.18f) // Playful, energetic speech rate
            }
        } else {
            Log.e("TtsManager", "TextToSpeech initialization failed with status $status")
        }
    }

    fun speak(text: String, queueMode: Int = TextToSpeech.QUEUE_FLUSH) {
        if (isInitialized && !isMuted && text.isNotBlank()) {
            tts?.speak(text, queueMode, null, "AIM_BOT_TTS_${System.currentTimeMillis()}")
        }
    }

    fun vocalizeTabChange(tab: RobotTab) {
        val announcement = when (tab) {
            RobotTab.HOME -> "Beep boop! Hello buddy! I am ready!"
            RobotTab.EXPLORE -> "Yay! Let's go explore together!"
            RobotTab.RC -> "Wahoo! Remote control mode online!"
            RobotTab.VISION -> "Oooh look! Camera scanning mode active!"
            RobotTab.ASSISTANT -> "Bzzz! Smart voice assistant ready, ask me anything!"
            RobotTab.ENGINEERING -> "Bip bip! Engineering telemetry online!"
            RobotTab.SETTINGS -> "Settings menu opened! Customize my options!"
            RobotTab.UNDERSTANDING_AIMBOT -> "Aww! Check out my cute eye expressions guide!"
        }
        speak(announcement)
    }

    fun toggleMute(): Boolean {
        isMuted = !isMuted
        if (!isMuted) {
            speak("Voice synthesizer unmuted. System ready.")
        } else {
            stop()
        }
        return isMuted
    }

    fun isMuted(): Boolean = isMuted

    fun stop() {
        tts?.stop()
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
