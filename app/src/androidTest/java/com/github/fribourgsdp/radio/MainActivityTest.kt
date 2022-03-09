package com.github.fribourgsdp.radio

import android.app.Activity
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Main Activity Tests
 *
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    var mainActivityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun correctTextOnTextView() {
        // Context of the app under test.
        val txtView = Espresso.onView(ViewMatchers.withId(R.id.mainText))

        txtView.check(
            ViewAssertions.matches(
                ViewMatchers.withText("Hello World!")
            )
        )
    }
    @Test
    fun correctTransitionToDisplayLyricsActivity(){
        Intents.init()
        Espresso.onView(ViewMatchers.withId(R.id.button)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(DisplayLyricsActivity::class.java.name))
        Intents.release()
    }
}