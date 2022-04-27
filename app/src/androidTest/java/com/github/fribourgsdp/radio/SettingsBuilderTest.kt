package com.github.fribourgsdp.radio

import org.junit.Assert
import org.junit.Test

class SettingsBuilderTest {
    @Test
    fun changeLanguageWork(){
        val settings = Settings.createDefaultSettings().getResult()
        val settingsBuilder = SettingsBuilder(settings)
        val settings2 = settingsBuilder.language(Language.FRENCH).build()
        Assert.assertEquals(Language.FRENCH, settings2.getLanguage())
    }
}