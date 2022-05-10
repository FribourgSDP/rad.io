package com.github.fribourgsdp.radio.activities

import android.content.Context
import androidx.test.espresso.Espresso
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onData
import com.github.fribourgsdp.radio.MainActivity
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.config.Settings
import com.github.fribourgsdp.radio.config.SettingsActivity
import com.github.fribourgsdp.radio.config.language.Language
import com.github.fribourgsdp.radio.config.language.LanguageManager
import com.github.fribourgsdp.radio.mockimplementations.MockFileSystem
import com.github.fribourgsdp.radio.mockimplementations.MockSettingsActivity
import org.hamcrest.Matchers.*


@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {
  @get:Rule
    var settingsActivityRule = ActivityScenarioRule(MockSettingsActivity::class.java)

    private val ctx: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun setup() {
        // Settings.setFSGetter(MockFileSystem.MockFSGetter)
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun onBackPressedGoesToMainActivity() {
        Espresso.pressBack()
        Intents.intended(
            allOf(
                IntentMatchers.hasComponent(MainActivity::class.java.name)
            )
        )
    }


    // TODO: Mock the LanguageManager so that the tests don't change the emulator mid run
    /*
    @Test
    fun saveSettingsWork() {
        val spinnerId = Espresso.onView(ViewMatchers.withId(R.id.spinner_language))

        spinnerId.perform(ViewActions.click())
        onData(allOf(`is`(instanceOf(Language::class.java)),`is`(Language.FRENCH)))
            .perform(ViewActions.click())

        Intents.intended(
            allOf(
                IntentMatchers.hasComponent(SettingsActivity::class.java.name),
                IntentMatchers.toPackage("com.github.fribourgsdp.radio")
            )
        )
    }
    */
}