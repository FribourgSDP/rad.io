package com.github.fribourgsdp.radio

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {
  @get:Rule
    var settingsActivityRule = ActivityScenarioRule(SettingsActivity::class.java)

    private val ctx: Context = ApplicationProvider.getApplicationContext()

    @Before
    fun initIntent() {
        Intents.init()
    }

    @After
    fun releaseIntent() {
        Intents.release()
    }
/*
    @Test
    fun onBackPressedGoesToMainActivity() {
        Espresso.pressBack()
        Intents.intended(
            allOf(
                IntentMatchers.hasComponent(MainActivity::class.java.name)
            )
        )
    }


    @Test
    fun saveSettingsWork() {

        val spinnerId = Espresso.onView(ViewMatchers.withId(R.id.spinner_language))

        spinnerId.perform(ViewActions.click())
        onData(allOf(`is`(instanceOf(Language::class.java)),`is`(Language.FRENCH))).perform(ViewActions.click())
        Intents.intended(
            allOf(
                IntentMatchers.hasComponent(SettingsActivity::class.java.name),
                IntentMatchers.toPackage("com.github.fribourgsdp.radio")
            )
        )
    }

 */
}