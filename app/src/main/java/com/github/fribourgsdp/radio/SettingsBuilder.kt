package com.github.fribourgsdp.radio

import android.content.Context
import io.agora.rtc.IRtcEngineEventHandler


class SettingsBuilder {
    private lateinit var language : Language

    constructor(settings : Settings){
        language = settings.getLanguage()
    }

    fun language(language : Language):  SettingsBuilder {
        this.language = language
        return this
    }

    fun build():  Settings {
        return Settings(language)
    }
}