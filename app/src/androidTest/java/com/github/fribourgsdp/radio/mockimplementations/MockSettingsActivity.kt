package com.github.fribourgsdp.radio.mockimplementations

import android.content.Context
import android.os.Bundle
import android.util.Log
import com.github.fribourgsdp.radio.config.Settings
import com.github.fribourgsdp.radio.config.SettingsActivity
import com.github.fribourgsdp.radio.config.language.LanguageManager
import org.mockito.Mockito

class MockSettingsActivity : SettingsActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Settings.setFSGetter(MockFileSystem.MockFSGetter)
        Settings.createDefaultSettings().save(Mockito.mock(Context::class.java))
        super.onCreate(savedInstanceState)
    }

    override fun getLanguageManager() : LanguageManager{
        return MockLanguageManager(this)
    }
}