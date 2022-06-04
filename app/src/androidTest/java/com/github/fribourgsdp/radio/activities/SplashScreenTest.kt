package com.github.fribourgsdp.radio.activities

import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.fribourgsdp.radio.MainActivity
import com.github.fribourgsdp.radio.SplashScreen
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Splash screen Tests
 *
 */
@RunWith(AndroidJUnit4::class)
class SplashScreenTest {

    @get:Rule
    var splashRule = ActivityScenarioRule(SplashScreen::class.java)

    @Before
    fun initIntent() {
        Intents.init()
    }

    @After
    fun releaseIntent() {
        Intents.release()
    }

    @Test
    fun splashScreenSendsToMain() {
        // wait for the screen to finish
        Thread.sleep(SplashScreen.LOAD_TIME_IN_MILLIS + 1)

        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(MainActivity::class.java.name),
                IntentMatchers.toPackage(packageName)
            )
        )
    }

}