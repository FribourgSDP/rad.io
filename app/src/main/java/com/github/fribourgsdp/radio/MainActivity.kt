package com.github.fribourgsdp.radio

import android.app.Activity
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import java.util.*

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    var t1: TextToSpeech? = null
    var ed1: EditText? = null
    var b1: Button? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ed1 = findViewById<View>(R.id.zzz) as EditText
        b1 = findViewById<View>(R.id.VoiceOverIpButton) as Button
        t1 = TextToSpeech(
            applicationContext
        ) { status ->
            if (status != TextToSpeech.ERROR) {
                t1!!.language = Locale.UK
            }
        }
        b1!!.setOnClickListener {
            val toSpeak = ed1!!.text.toString()
            Toast.makeText(applicationContext, toSpeak, Toast.LENGTH_SHORT).show()
            saySomething(toSpeak, TextToSpeech.QUEUE_ADD)

        }
    }
    override fun onInit(status: Int) {
        // check the results in status variable.
        if (status == TextToSpeech.SUCCESS) {
            // setting the language to the default phone language.
            val ttsLang = t1?.setLanguage(Locale.getDefault())
            // check if the language is supportable.
            if (ttsLang == TextToSpeech.LANG_MISSING_DATA || ttsLang == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(this, "We can't support your language", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(this, "TTS Initialization failed!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saySomething(something: String, queueMode: Int = TextToSpeech.QUEUE_ADD) {
        val speechStatus = t1?.speak(something, queueMode, null, "ID")
        if (speechStatus == TextToSpeech.ERROR) {
            Toast.makeText(this, "Cant use the Text to speech.", Toast.LENGTH_LONG).show()
        }
    }


    public override fun onPause() {
        if (t1 != null) {
            t1!!.stop()
            t1!!.shutdown()
        }
        super.onPause()
    }
}