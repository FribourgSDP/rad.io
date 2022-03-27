package com.github.fribourgsdp.radio

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.fribourgsdp.radio.activities.*
import com.github.fribourgsdp.radio.backend.mockimplementations.LocalDatabase
import com.github.fribourgsdp.radio.activities.mockimplementations.*
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Join Game Activity Tests with working database
 * NB: Here we test on [WorkingJoinGameActivity], that uses a [LocalDatabase],
 * so that the tests don't depend on firebase.
 *
 */
@RunWith(AndroidJUnit4::class)
class WorkingJoinGameActivityTest {
    @get:Rule
    var gameSettingsActivityRule = ActivityScenarioRule(WorkingJoinGameActivity::class.java)

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
    fun intentWorksWithCorrectSettings() {
        // Test values
        val testUID = 567L

        val joinButton = Espresso.onView(ViewMatchers.withId(R.id.joinGameButton))

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

        Intents.intended(
            Matchers.allOf(
                IntentMatchers.toPackage("com.github.fribourgsdp.radio"),
                IntentMatchers.hasComponent(LobbyActivity::class.java.name),
                IntentMatchers.hasExtra(GAME_HOST_KEY, LocalDatabase.EXPECTED_SETTINGS.host.name),
                IntentMatchers.hasExtra(GAME_NAME_KEY, LocalDatabase.EXPECTED_SETTINGS.name),
                IntentMatchers.hasExtra(GAME_PLAYLIST_KEY, LocalDatabase.EXPECTED_SETTINGS.playlist.name),
                IntentMatchers.hasExtra(GAME_NB_ROUNDS_KEY, LocalDatabase.EXPECTED_SETTINGS.nbRounds),
                IntentMatchers.hasExtra(GAME_HINT_KEY, LocalDatabase.EXPECTED_SETTINGS.withHint),
                IntentMatchers.hasExtra(GAME_PRIVACY_KEY, LocalDatabase.EXPECTED_SETTINGS.isPrivate),
                IntentMatchers.hasExtra(GAME_IS_HOST_KEY, false),
                IntentMatchers.hasExtra(GAME_UID_KEY, testUID)

            )
        )
    }

}