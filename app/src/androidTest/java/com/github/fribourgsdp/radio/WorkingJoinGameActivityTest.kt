package com.github.fribourgsdp.radio

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.fribourgsdp.radio.mockimplementations.LocalDatabase
import com.github.fribourgsdp.radio.mockimplementations.WorkingJoinGameActivity
import com.github.fribourgsdp.radio.utils.CustomMatchers.Companion.atPosition
import org.hamcrest.Matchers.*
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

        val joinButton = onView(withId(R.id.joinGameButton))

        joinButton.check(matches(isNotEnabled()))

        onView(withId(R.id.gameToJoinID))
            .perform(ViewActions.typeText(testUID.toString()))

        closeSoftKeyboard()

        joinButton.check(matches(isEnabled()))

        joinButton.perform(ViewActions.click())

        Intents.intended(
            allOf(
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

        checkLobbiesDisplay(lobbies)
    }

    @Test
    fun spinnerChoiceSortsLobbies() {
        val lobbies = ArrayList(WorkingJoinGameActivity.testDatabase.lobbies)

        // Sort them by name
        lobbies.sortBy { it.name }

        // open the spinner
        onView(withId(R.id.lobbySortSpinner))
            .perform(click())
        // click on sort value
        onView(withText(ctx.getString(R.string.name)))
            .perform(click());

        checkLobbiesDisplay(lobbies)

        // Sort them by host name
        lobbies.sortBy { it.hostName }

        // open the spinner
        onView(withId(R.id.lobbySortSpinner))
            .perform(click())
        // click on sort value
        onView(withText(ctx.getString(R.string.hostname)))
            .perform(click());

        checkLobbiesDisplay(lobbies)

        // Sort them by id
        lobbies.sortBy { it.id }

        // open the spinner
        onView(withId(R.id.lobbySortSpinner))
            .perform(click())
        // click on sort value
        onView(withText(ctx.getString(R.string.id)))
            .perform(click());

        checkLobbiesDisplay(lobbies)
    }

    private fun checkLobbiesDisplay(expected: List<LobbyData>) {
        // Check that the scores are displayed with the correct data and in the correct order
        onView(withId(R.id.publicLobbiesRecyclerView))
            .check(
                matches(
                    allOf(
                        // First lobby
                        atPosition(
                            0, R.id.lobbyIdTextView,
                            withText("${expected[0].id}")
                        ),
                        atPosition(
                            0, R.id.lobbyNameTextView,
                            withText(ctx.getString(R.string.game_name_format, expected[0].name))
                        ),
                        atPosition(
                            0, R.id.lobbyHostNameTextView,
                            withText(ctx.getString(R.string.host_name_format, expected[0].hostName))
                        ),
                        // Second lobby
                        atPosition(
                            1, R.id.lobbyIdTextView,
                            withText("${expected[1].id}")
                        ),
                        atPosition(
                            1, R.id.lobbyNameTextView,
                            withText(ctx.getString(R.string.game_name_format, expected[1].name))
                        ),
                        atPosition(
                            1, R.id.lobbyHostNameTextView,
                            withText(ctx.getString(R.string.host_name_format, expected[1].hostName))
                        ),
                        // Third lobby
                        atPosition(
                            2, R.id.lobbyIdTextView,
                            withText("${expected[2].id}")
                        ),
                        atPosition(
                            2, R.id.lobbyNameTextView,
                            withText(ctx.getString(R.string.game_name_format, expected[2].name))
                        ),
                        atPosition(
                            2, R.id.lobbyHostNameTextView,
                            withText(ctx.getString(R.string.host_name_format, expected[2].hostName))
                        ),
                    )
                )
            )
    }

}