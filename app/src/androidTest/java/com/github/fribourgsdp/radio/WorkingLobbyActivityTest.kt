package com.github.fribourgsdp.radio

import com.github.fribourgsdp.radio.mockimplementations.*

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Lobby Activity Tests with working database
 * NB: Here we test on [WorkingLobbyActivity], that uses a [LocalDatabase],
 * so that the tests don't depend on firebase.
 *
 */
@RunWith(AndroidJUnit4::class)
class WorkingLobbyActivityTest {
    @get:Rule
    var lobbyActivityRule = ActivityScenarioRule(WorkingLobbyActivity::class.java)

    private val ctx: Context = ApplicationProvider.getApplicationContext()

    private val json = Json {
        allowStructuredMapKeys = true
        ignoreUnknownKeys = true
    }

    @Before
    fun initIntent() {
        Intents.init()
    }

    @After
    fun releaseIntent() {
        Intents.release()
    }

    @Test
    fun lobbyDisplayCorrectInfosWhenHost() {
        // Test values
        val testHost = User("The Boss")
        val testName = "Hello World!"
        val testPlaylist = Playlist("Rap Playlist")
        val testNbRounds = 20
        val withHint = true
        val private = true

        // Init views
        val uuidTextView = Espresso.onView(withId(R.id.uuidText))
        val hostNameTextView = Espresso.onView(withId(R.id.hostNameText))
        val gameNameTextView = Espresso.onView(withId(R.id.gameNameText))
        val playlistTextView = Espresso.onView(withId(R.id.playlistText))
        val nbRoundsTextView = Espresso.onView(withId(R.id.nbRoundsText))
        val withHintTextView = Espresso.onView(withId(R.id.withHintText))
        val privateTextView  = Espresso.onView(withId(R.id.privateText))
        val launchGameButton = Espresso.onView(withId(R.id.launchGameButton))

        val testIntent = Intent(ctx, WorkingLobbyActivity::class.java).apply {
            putExtra(GAME_HOST_KEY, json.encodeToString(testHost))
            putExtra(GAME_NAME_KEY, testName)
            putExtra(GAME_PLAYLIST_KEY, json.encodeToString(testPlaylist))
            putExtra(GAME_NB_ROUNDS_KEY, testNbRounds)
            putExtra(GAME_HINT_KEY, withHint)
            putExtra(GAME_PRIVACY_KEY, private)
            putExtra(GAME_IS_HOST_KEY, true)
        }

        ActivityScenario.launch<WorkingLobbyActivity>(testIntent).use {
            uuidTextView.check(matches(withText(ctx.getString(R.string.uid_text_format, LocalDatabase.EXPECTED_UID))))
            hostNameTextView.check(matches(withText(ctx.getString(R.string.host_name_format, testHost.name))))
            gameNameTextView.check(matches(withText(ctx.getString(R.string.game_name_format, testName))))
            playlistTextView.check(matches(withText(ctx.getString(R.string.playlist_format, testPlaylist.name))))
            nbRoundsTextView.check(matches(withText(ctx.getString(R.string.number_of_rounds_format, testNbRounds))))
            withHintTextView.check(matches(withText(ctx.getString(R.string.hints_enabled_format, withHint))))
            privateTextView.check(matches(withText(ctx.getString(R.string.private_format, private))))
            launchGameButton.check(matches(isDisplayed()))
        }

    }
    
    @Test
    fun lobbyDisplayCorrectInfosWhenInvited() {
        // Test values
        val testUID = 42L
        val testHost = User("The Boss")
        val testName = "Hello World!"
        val testPlaylist = Playlist("Rap Playlist")
        val testNbRounds = 20
        val withHint = true
        val private = true

        // Init views
        val uuidTextView = Espresso.onView(withId(R.id.uuidText))
        val hostNameTextView = Espresso.onView(withId(R.id.hostNameText))
        val gameNameTextView = Espresso.onView(withId(R.id.gameNameText))
        val playlistTextView = Espresso.onView(withId(R.id.playlistText))
        val nbRoundsTextView = Espresso.onView(withId(R.id.nbRoundsText))
        val withHintTextView = Espresso.onView(withId(R.id.withHintText))
        val privateTextView  = Espresso.onView(withId(R.id.privateText))
        val launchGameButton = Espresso.onView(withId(R.id.launchGameButton))

        val testIntent = Intent(ctx, WorkingLobbyActivity::class.java).apply {
            putExtra(GAME_UID_KEY, testUID)
            putExtra(GAME_HOST_KEY, json.encodeToString(testHost))
            putExtra(GAME_NAME_KEY, testName)
            putExtra(GAME_PLAYLIST_KEY, json.encodeToString(testPlaylist))
            putExtra(GAME_NB_ROUNDS_KEY, testNbRounds)
            putExtra(GAME_HINT_KEY, withHint)
            putExtra(GAME_PRIVACY_KEY, private)
            putExtra(GAME_IS_HOST_KEY, false)
        }

        ActivityScenario.launch<WorkingLobbyActivity>(testIntent).use {
            uuidTextView.check(matches(withText(ctx.getString(R.string.uid_text_format, testUID))))
            hostNameTextView.check(matches(withText(ctx.getString(R.string.host_name_format, testHost.name))))
            gameNameTextView.check(matches(withText(ctx.getString(R.string.game_name_format, testName))))
            playlistTextView.check(matches(withText(ctx.getString(R.string.playlist_format, testPlaylist.name))))
            nbRoundsTextView.check(matches(withText(ctx.getString(R.string.number_of_rounds_format, testNbRounds))))
            withHintTextView.check(matches(withText(ctx.getString(R.string.hints_enabled_format, withHint))))
            privateTextView.check(matches(withText(ctx.getString(R.string.private_format, private))))
            launchGameButton.check(matches(not(isDisplayed())))
        }
    }

    @Test
    fun displayErrorMessageOnBadUIDWhenInvited() {
        // Init views
        val uuidTextView = Espresso.onView(withId(R.id.uuidText))

        val testIntent = Intent(ctx, WorkingLobbyActivity::class.java).apply {
            putExtra(GAME_UID_KEY, -1)
            putExtra(GAME_IS_HOST_KEY, false)
        }

        ActivityScenario.launch<WorkingLobbyActivity>(testIntent).use {
            uuidTextView.check(matches(withText(ctx.getString(R.string.uid_error_join))))
        }
    }

}