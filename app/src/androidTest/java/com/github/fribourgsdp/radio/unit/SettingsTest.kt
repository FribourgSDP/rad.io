package com.github.fribourgsdp.radio.unit

import android.content.Context
import com.github.fribourgsdp.radio.config.Settings
import com.github.fribourgsdp.radio.config.language.Language
import com.github.fribourgsdp.radio.mockimplementations.MockFileSystem
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito

class SettingsTest {
    @Before
    fun setup() {
        Settings.setFSGetter(MockFileSystem.MockFSGetter)
    }

    @Test
    fun getCorrectLanguage(){
        val settings = Settings(Language.FRENCH)
        Assert.assertEquals(Language.FRENCH, settings.language)
    }

    @Test
    fun saveAndLoadWorkCorrectly(){
        val ctx = Mockito.mock(Context::class.java)
        val settings = Settings(Language.FRENCH)
        settings.save(ctx)
        val settings2 = Settings.loadOrDefault(ctx)
        Assert.assertEquals(Language.FRENCH, settings2.language)
    }

}