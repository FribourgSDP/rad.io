package com.github.fribourgsdp.radio.unit

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.github.fribourgsdp.radio.config.Settings
import com.github.fribourgsdp.radio.config.language.Language
import org.junit.Assert
import org.junit.Test

class SettingsTest {

    @Test
    fun getCorrectLanguage(){
        val settings = Settings(Language.FRENCH)
        Assert.assertEquals(Language.FRENCH, settings.language)
    }

    @Test
    fun saveAndLoadWorkCorrectly(){
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val companion = Settings.Companion
        val settings = Settings(Language.FRENCH)
        settings.save(ctx)
        val settings2 = companion.loadOrDefault(ctx)
        Assert.assertEquals(Language.FRENCH, settings2.language)
    }


}