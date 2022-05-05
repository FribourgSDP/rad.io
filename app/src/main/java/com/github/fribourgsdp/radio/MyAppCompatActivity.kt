package com.github.fribourgsdp.radio

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.fribourgsdp.radio.config.language.LanguageManager

open class MyAppCompatActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val languageManager  = LanguageManager(this)
        languageManager.getLang()?.let { languageManager.updateResource(it) }
    }
}