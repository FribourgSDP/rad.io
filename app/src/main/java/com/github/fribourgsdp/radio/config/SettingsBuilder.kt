package com.github.fribourgsdp.radio.config

import com.github.fribourgsdp.radio.config.language.Language


class SettingsBuilder {
    private lateinit var language : Language

    constructor(settings : Settings){
        language = settings.getLanguage()
    }

    fun language(language : Language): SettingsBuilder {
        this.language = language
        return this
    }

    fun build(): Settings {
        return Settings(language)
    }
}