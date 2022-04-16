package com.github.fribourgsdp.radio

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before


import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*

/**
 * Main Activity Tests
 *
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private val ctx: Context = ApplicationProvider.getApplicationContext()


    @get:Rule
    var mainActivityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun initIntent() {
        Intents.init()
    }

    @After
    fun releaseIntent() {
        Intents.release()
    }
    @Test
    fun playButtonStartsGameSettings() {
        val playButton = Espresso.onView(withId(R.id.playButton))
        playButton.perform(ViewActions.click())

        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(GameSettingsActivity::class.java.name),
                IntentMatchers.toPackage("com.github.fribourgsdp.radio")
            )
        )
    }

    @Test
    fun settingsButtonStartsSettings() {
        val settingsButton = Espresso.onView(withId(R.id.settingsButton))
        settingsButton.perform(ViewActions.click())

        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(SettingsActivity::class.java.name),
                IntentMatchers.toPackage("com.github.fribourgsdp.radio")
            )
        )
    }

    @Test
    fun correctTransitionToDisplayLyricsActivity(){
        Espresso.onView(withId(R.id.button)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(DisplayLyricsActivity::class.java.name))
    }

    @Test
    fun correctTransitionToUserProfile(){
        Espresso.onView(withId(R.id.profileButton)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(UserProfileActivity::class.java.name))
    }

    @Test
    fun correctTransitionToVoiceOverIpActivity(){
        Espresso.onView(withId(R.id.VoiceOverIpButton)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(VoiceOverIPActivity::class.java.name))
    }
}