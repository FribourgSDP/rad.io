package com.github.fribourgsdp.radio

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.fribourgsdp.radio.mockimplementations.LocalDatabase
import com.github.fribourgsdp.radio.mockimplementations.WorkingJoinGameActivity
import com.github.fribourgsdp.radio.utils.CustomMatchers.Companion.atPosition
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
        // Test values => this one cannot exist, so it won't look in the database
        val testUID = 1001L

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
                IntentMatchers.hasExtra(GAME_HOST_NAME_KEY, LocalDatabase.EXPECTED_SETTINGS.hostName),
                IntentMatchers.hasExtra(GAME_NAME_KEY, LocalDatabase.EXPECTED_SETTINGS.name),
                IntentMatchers.hasExtra(GAME_PLAYLIST_NAME_KEY, LocalDatabase.EXPECTED_SETTINGS.playlistName),
                IntentMatchers.hasExtra(GAME_NB_ROUNDS_KEY, LocalDatabase.EXPECTED_SETTINGS.nbRounds),
                IntentMatchers.hasExtra(GAME_HINT_KEY, LocalDatabase.EXPECTED_SETTINGS.withHint),
                IntentMatchers.hasExtra(GAME_PRIVACY_KEY, LocalDatabase.EXPECTED_SETTINGS.isPrivate),
                IntentMatchers.hasExtra(GAME_IS_HOST_KEY, false),
                IntentMatchers.hasExtra(GAME_UID_KEY, testUID)

            )
        )
    }

    @Test
    fun publicLobbiesDisplay() {
        val lobbies = ArrayList(WorkingJoinGameActivity.testDatabase.lobbies)

        // Check that the scores are displayed with the correct data and in the correct order
        onView(ViewMatchers.withId(R.id.publicLobbiesRecyclerView))
            .check(
                ViewAssertions.matches(
                    Matchers.allOf(
                        // First lobby
                        atPosition(
                            0, R.id.lobbyIdTextView,
                            ViewMatchers.withText("${lobbies[0].id}")
                        ),
                        atPosition(
                            0, R.id.lobbyNameTextView,
                            ViewMatchers.withText(ctx.getString(R.string.game_name_format, lobbies[0].name))
                        ),
                        atPosition(
                            0, R.id.lobbyHostNameTextView,
                            ViewMatchers.withText(ctx.getString(R.string.host_name_format, lobbies[0].hostName))
                        ),
                        // Second lobby
                        atPosition(
                            1, R.id.lobbyIdTextView,
                            ViewMatchers.withText("${lobbies[1].id}")
                        ),
                        atPosition(
                            1, R.id.lobbyNameTextView,
                            ViewMatchers.withText(ctx.getString(R.string.game_name_format, lobbies[1].name))
                        ),
                        atPosition(
                            1, R.id.lobbyHostNameTextView,
                            ViewMatchers.withText(ctx.getString(R.string.host_name_format, lobbies[1].hostName))
                        ),
                        // Third lobby
                        atPosition(
                            2, R.id.lobbyIdTextView,
                            ViewMatchers.withText("${lobbies[2].id}")
                        ),
                        atPosition(
                            2, R.id.lobbyNameTextView,
                            ViewMatchers.withText(ctx.getString(R.string.game_name_format, lobbies[2].name))
                        ),
                        atPosition(
                            2, R.id.lobbyHostNameTextView,
                            ViewMatchers.withText(ctx.getString(R.string.host_name_format, lobbies[2].hostName))
                        ),
                    )
                )
            )
    }

}