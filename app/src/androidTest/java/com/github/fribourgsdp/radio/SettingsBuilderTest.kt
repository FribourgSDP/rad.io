package com.github.fribourgsdp.radio

import com.github.fribourgsdp.radio.config.Settings
import com.github.fribourgsdp.radio.config.SettingsBuilder
import org.junit.Assert
import org.junit.Test

class SettingsBuilderTest {
    @Test
    fun changeLanguageWork(){
        val settings = Settings.createDefaultSettings()
        val settingsBuilder = SettingsBuilder(settings)
        val settings2 = settingsBuilder.language(Language.FRENCH).build()
        Assert.assertEquals(Language.FRENCH, settings2.getLanguage())
    }
}