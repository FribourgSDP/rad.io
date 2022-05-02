package com.github.fribourgsdp.radio

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class FireBaseTestActivityTest {
/**
    @get:Rule
    var FireBaseTestRule = ActivityScenarioRule(FireBaseTestActivity::class.java)

    private val ctx: Context = ApplicationProvider.getApplicationContext()


    @Before
    fun initIntent() {
        Intents.init()
    }

    @After
    fun releaseIntent() {
        Intents.release()
    }


    //this class is to test the Database feature easily, it doesn't have any functionality so I just run the activity to have the coverage
    @Test
    fun launchTheActivity(){

    }

    @Test
    fun backButtonGoesToMainActivity() {
        Espresso.pressBack()
        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(MainActivity::class.java.name)
            )
        )
    }
**/

}