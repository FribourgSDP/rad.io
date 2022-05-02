package com.github.fribourgsdp.radio

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
import org.hamcrest.Matchers.*


@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {
    /**
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


    @Test
    fun onBackPressedGoesToMainActivity() {
        Espresso.pressBack()
        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(MainActivity::class.java.name)
            )
        )
    }


    @Test
    fun saveSettingsWork() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, SettingsActivity::class.java)
        ActivityScenario.launch<SettingsActivity>(intent).use { scenario ->

            val spinnerId = Espresso.onView(ViewMatchers.withId(R.id.spinner_language))

            spinnerId.perform(ViewActions.click())
            onData(allOf(`is`(instanceOf(Language::class.java)),`is`(Language.FRENCH))).perform(ViewActions.click())
            Intents.intended(
                Matchers.allOf(
                    IntentMatchers.hasComponent(SettingsActivity::class.java.name),
                    IntentMatchers.toPackage("com.github.fribourgsdp.radio")
                )
            )

        }
    }**/
}