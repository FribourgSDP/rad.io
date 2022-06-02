package com.github.fribourgsdp.radio.activities

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers.isTouchable
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.fribourgsdp.radio.MainActivity
import com.github.fribourgsdp.radio.data.LobbyData
import com.github.fribourgsdp.radio.data.LobbyDataKeys
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.config.language.LanguageManager
import com.github.fribourgsdp.radio.deprecated.VoiceOverIPActivity
import com.github.fribourgsdp.radio.game.prep.*
import com.github.fribourgsdp.radio.mockimplementations.*
import com.github.fribourgsdp.radio.mockimplementations.WorkingJoinGameActivity
import com.github.fribourgsdp.radio.utils.CustomMatchers.Companion.atPosition
import org.hamcrest.Matchers
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
    var joinActivityRule = ActivityScenarioRule(WorkingJoinGameActivity::class.java)

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

        joinButton.perform(click())

        Intents.intended(
            allOf(
                IntentMatchers.toPackage("com.github.fribourgsdp.radio"),
                IntentMatchers.hasComponent(LobbyActivity::class.java.name),
                IntentMatchers.hasExtra(GAME_HOST_NAME_KEY, LocalDatabase.EXPECTED_SETTINGS.hostName),
                IntentMatchers.hasExtra(GAME_NAME_KEY, LocalDatabase.EXPECTED_SETTINGS.name),
                IntentMatchers.hasExtra(GAME_PLAYLIST_NAME_KEY,LocalDatabase.EXPECTED_SETTINGS.playlistName),
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
    fun pressBackWorks(){
        val context: Context = ApplicationProvider.getApplicationContext()

        val intent = Intent(context, VoiceOverIPActivity::class.java)
        ActivityScenario.launch<VoiceOverIPActivity>(intent).use { _ ->
            pressBack()
            Intents.intended(
                allOf(
                    IntentMatchers.hasComponent(MainActivity::class.java.name),
                    IntentMatchers.toPackage("com.github.fribourgsdp.radio")
                )
            )
        }
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
                            withText(expected[0].id.toString())
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
                            withText(expected[1].id.toString())
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
                            withText(expected[2].id.toString())
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
