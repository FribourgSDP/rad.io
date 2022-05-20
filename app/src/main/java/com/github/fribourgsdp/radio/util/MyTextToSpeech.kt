package com.github.fribourgsdp.radio.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.config.language.LanguageManager
import com.github.fribourgsdp.radio.external.musixmatch.MusixmatchLyricsGetter
import java.util.*


const val TTS_INITIALIZATION_RETRY_DELAY_MS = 1000

class MyTextToSpeech(private val applicationContext: Context) : TextToSpeech.OnInitListener {
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
            val ttsLangStatus = tts!!.setLanguage(locale)
            // check if the language is supportable.
            if (ttsLangStatus == TextToSpeech.LANG_MISSING_DATA || ttsLangStatus == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(
                    applicationContext,
                    applicationContext.getString(R.string.ttsWeCantSupportYourLanguage),
                    Toast.LENGTH_LONG
                ).show()
            }
        } else {
            Toast.makeText(
                applicationContext,
                applicationContext.getString(R.string.TextToSpeechInitializationFailed),
                Toast.LENGTH_SHORT
            ).show()
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
    fun readLyrics(lyrics : String){
        Toast.makeText(applicationContext, "TTS", Toast.LENGTH_SHORT).show()
        val speechStatus = tts?.speak(MusixmatchLyricsGetter.makeReadable(lyrics), TextToSpeech.QUEUE_FLUSH, null, "ID")
        if(speechStatus == TextToSpeech.ERROR){
            Toast.makeText(applicationContext, applicationContext.getString(R.string.cantUseTextToSpeech), Toast.LENGTH_LONG).show()
            val t = Thread {
                Thread.sleep(TTS_INITIALIZATION_RETRY_DELAY_MS.toLong())
                readLyrics(lyrics)
            }
        }
    }

    /**
     * Function to be called whenever the activity pauses to make sure the TTS engine is disabled.
     */
    fun onPause(){
        if (tts != null) {
            tts!!.stop()
            tts!!.shutdown()
        }
    }
}