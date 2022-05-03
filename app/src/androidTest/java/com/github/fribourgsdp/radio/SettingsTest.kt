package com.github.fribourgsdp.radio

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert
import org.junit.Test

class SettingsTest {

    @Test
    fun getCorrectLanguage(){
        val settings = Settings(Language.FRENCH)
        Assert.assertEquals(Language.FRENCH, settings.getLanguage())
    }

    @Test
    fun saveAndLoadWorkCorrectly(){
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val companion = Settings.Companion
        val settings = Settings(Language.FRENCH)
        settings.save(ctx)
        val settings2 = companion.loadOrDefault(ctx).getResult()
        Assert.assertEquals(Language.FRENCH, settings2.getLanguage())
    }


}