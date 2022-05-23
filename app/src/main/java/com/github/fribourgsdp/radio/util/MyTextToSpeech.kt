package com.github.fribourgsdp.radio.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.config.language.LanguageManager
import com.github.fribourgsdp.radio.external.musixmatch.MusixmatchLyricsGetter
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*


const val TTS_INITIALIZATION_RETRY_DELAY_MS : Long = 1000

open class MyTextToSpeech(private val applicationContext: Context) : TextToSpeech.OnInitListener {
    private var tts: TextToSpeech? = null

    override fun onInit(status: Int) {
        // check the results in status variable.
        if (status == TextToSpeech.SUCCESS) {
            // setting the language to the default phone language.
            val language = LanguageManager(applicationContext).getLang()
            val locale = if (language == "fr") {
                Locale.FRANCE
            } else {
                Locale.UK
            }
            val ttsLangStatus = tts?.setLanguage(locale)
            // check if the language is supportable.
            if (ttsLangStatus == TextToSpeech.LANG_MISSING_DATA || ttsLangStatus == TextToSpeech.LANG_NOT_SUPPORTED) {
                toastDisplay(applicationContext.getString(R.string.ttsWeCantSupportYourLanguage))
            }
        } else {
            toastDisplay(applicationContext.getString(R.string.TextToSpeechInitializationFailed))
        }
    }

    /**
     * Initializes the text-to-speech engine if noSing=true
     */
    fun initTextToSpeech(noSing : Boolean) {
        if(!noSing) return
        this.tts = TextToSpeech(applicationContext, this)
    }


    /**
     * Reads the given lyrics, and display Toast messages accordingly.
     * If the TextToSpeech engine appears not to be initialized yet, spawns a new Thread and retries [TTS_INITIALIZATION_RETRY_DELAY_MS]ms later.
     */
    open fun readLyrics(lyrics : String){
        toastDisplay("TTS")
        val readableLyrics = MusixmatchLyricsGetter.makeReadable(lyrics)
        val speechStatus = tts?.speak(readableLyrics, TextToSpeech.QUEUE_FLUSH, null, "ID")
        if(speechStatus == TextToSpeech.ERROR){
            toastDisplay(applicationContext.getString(R.string.cantUseTextToSpeech))
            GlobalScope.launch {
                delay(TTS_INITIALIZATION_RETRY_DELAY_MS)
                tts?.speak(readableLyrics, TextToSpeech.QUEUE_ADD, null, "ID")
            }
        }
    }

    /**
     * Function to be called whenever the activity pauses to make sure the TTS engine is disabled.
     */
    fun onPause(){
        if (tts != null) {
            tts?.stop()
            tts?.shutdown()
        }
    }

    private fun toastDisplay(text : String){
        Toast.makeText(applicationContext, text, Toast.LENGTH_LONG).show()
    }
}