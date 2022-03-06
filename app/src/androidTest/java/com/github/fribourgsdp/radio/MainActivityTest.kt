package com.github.fribourgsdp.radio

import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
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
        var txtView = Espresso.onView(ViewMatchers.withId(R.id.mainText))

        txtView.check(
            ViewAssertions.matches(
                ViewMatchers.withText("Hello World!")
            )
        )
    }
}