package com.github.fribourgsdp.radio.activities

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.fribourgsdp.radio.MainActivity
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.config.SettingsActivity
import com.github.fribourgsdp.radio.config.language.Language
import com.github.fribourgsdp.radio.mockimplementations.MockSettingsActivity
import com.github.fribourgsdp.radio.utils.packageName
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith



@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {
    private val frenchLanguageName = "Français"
    @get:Rule
    var settingsActivityRule = ActivityScenarioRule(MockSettingsActivity::class.java)

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
            allOf(
                IntentMatchers.hasComponent(MainActivity::class.java.name)
            )
        )
    }


    @Test
    fun saveSettingsWork() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, MockSettingsActivity::class.java)
        ActivityScenario.launch<MockSettingsActivity>(intent).use {

            val spinnerId = Espresso.onView(ViewMatchers.withId(R.id.spinner_language))

            spinnerId.perform(ViewActions.click())
            onData(allOf(`is`(instanceOf(Language::class.java)),`is`(Language.FRENCH))).perform(ViewActions.click())
            Intents.intended(
                allOf(
                    IntentMatchers.hasComponent(SettingsActivity::class.java.name),
                    IntentMatchers.toPackage(packageName)
                )
            )

            Espresso.onView(ViewMatchers.withId(R.id.spinner_language))
                .check(ViewAssertions.matches(ViewMatchers.withSpinnerText(frenchLanguageName)))

        }
    }
}


