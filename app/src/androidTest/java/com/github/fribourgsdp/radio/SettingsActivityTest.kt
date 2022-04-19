package com.github.fribourgsdp.radio

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView

import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.CursorMatchers.withRowString
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import org.hamcrest.Matchers.*


@RunWith(AndroidJUnit4::class)
class SettingsActivityTest {

    @Before
    fun initIntent() {
        Intents.init()
    }

    @After
    fun releaseIntent() {
        Intents.release()
    }


    @Test
    fun correctTextOnTextView() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, GoogleSignInActivity::class.java)
        ActivityScenario.launch<GoogleSignInActivity>(intent).use { scenario ->

            val txtView = Espresso.onView(ViewMatchers.withId(R.id.captionTv))
            txtView.check(
                ViewAssertions.matches(
                    ViewMatchers.withText("Welcome to Google SignIn")
                )
            )
        }
    }

    @Test
    fun backPressedWorkCorrectly() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, SettingsActivity::class.java)
        ActivityScenario.launch<SettingsActivity>(intent).use { scenario ->

            Espresso.pressBack()
            Intents.intended(
                Matchers.allOf(
                    IntentMatchers.hasComponent(MainActivity::class.java.name),
                    IntentMatchers.toPackage("com.github.fribourgsdp.radio")
                )
            )
        }
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
    }




}