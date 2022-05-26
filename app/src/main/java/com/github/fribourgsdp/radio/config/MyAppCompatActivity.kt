package com.github.fribourgsdp.radio.config

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.fribourgsdp.radio.config.language.LanguageManager

open class MyAppCompatActivity : AppCompatActivity(),ConnectivityHolder {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val languageManager = LanguageManager(this)
        languageManager.getLang()?.let { languageManager.updateResource(it) }
    }


}