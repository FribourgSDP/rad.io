package com.github.fribourgsdp.radio

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.fribourgsdp.radio.mockimplementations.BuggyJoinGameActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Join Game Tests with buggy database
 * NB: Here we test on [BuggyJoinGameActivity], that uses a [BuggyDatabase],
 * so that the tests don't depend on firebase.
 *
 */
@RunWith(AndroidJUnit4::class)
class BuggyJoinGameActivityTest {
    /**
    @get:Rule
    var gameSettingsActivityRule = ActivityScenarioRule(BuggyJoinGameActivity::class.java)

    private val ctx: Context = ApplicationProvider.getApplicationContext()



    @Test
    fun errorDisplayedOnDBFail() {
        // Test values
        val testUID = 567L

        val joinButton = Espresso.onView(ViewMatchers.withId(R.id.joinGameButton))
        val errorTextView = Espresso.onView(ViewMatchers.withId(R.id.joinErrorView))

        joinButton.check(
            ViewAssertions.matches(
                ViewMatchers.isNotEnabled()
            )
        )

        Espresso.onView(ViewMatchers.withId(R.id.gameToJoinID))
            .perform(ViewActions.typeText(testUID.toString()))

        Espresso.closeSoftKeyboard()

        joinButton.check(
            ViewAssertions.matches(
                ViewMatchers.isEnabled()
            )
        )

        joinButton.perform(ViewActions.click())

        errorTextView.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        errorTextView.check(
            ViewAssertions.matches(
                ViewMatchers.withText(ctx.getString(R.string.lobby_not_found, testUID))
            )
        )

    }**/

}