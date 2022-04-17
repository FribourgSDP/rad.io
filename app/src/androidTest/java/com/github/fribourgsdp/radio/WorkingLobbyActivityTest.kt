package com.github.fribourgsdp.radio

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.test.InstrumentationRegistry.getTargetContext
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import com.github.fribourgsdp.radio.mockimplementations.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
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
    fun lobbyDisplayCorrectInfosWhenHost() {
        // Test values
        val testName = "Hello World!"
        val testPlaylist = Playlist("Rap Playlist")
        val testNbRounds = 20
        val withHint = true
        val private = true

        // Init views
        val uuidTextView = Espresso.onView(withId(R.id.uuidText))
        val gameNameTextView = Espresso.onView(withId(R.id.gameNameText))
        val hostNameTextView = Espresso.onView(withId(R.id.hostNameText))
        val playlistTextView = Espresso.onView(withId(R.id.playlistText))
        val nbRoundsTextView = Espresso.onView(withId(R.id.nbRoundsText))
        val withHintTextView = Espresso.onView(withId(R.id.withHintText))
        val privateTextView  = Espresso.onView(withId(R.id.privateText))
        val launchGameButton = Espresso.onView(withId(R.id.launchGameButton))

        val testIntent = Intent(ctx, WorkingLobbyActivity::class.java).apply {
            putExtra(GAME_NAME_KEY, testName)
            putExtra(GAME_PLAYLIST_KEY, Json.encodeToString(testPlaylist))
            putExtra(GAME_NB_ROUNDS_KEY, testNbRounds)
            putExtra(GAME_HINT_KEY, withHint)
            putExtra(GAME_PRIVACY_KEY, private)
            putExtra(GAME_IS_HOST_KEY, true)
        }

        ActivityScenario.launch<WorkingLobbyActivity>(testIntent).use {
            uuidTextView.check(matches(withText(ctx.getString(R.string.uid_text_format, LocalDatabase.EXPECTED_UID))))
            hostNameTextView.check(matches(withText(ctx.getString(R.string.host_name_format, "the best player"))))
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
        val testName = "Hello World!"
        val testPlaylist = Playlist("Rap Playlist")
        val testNbRounds = 20
        val withHint = true
        val private = true

        // Init views
        val uuidTextView = Espresso.onView(withId(R.id.uuidText))
        val gameNameTextView = Espresso.onView(withId(R.id.gameNameText))
        val playlistTextView = Espresso.onView(withId(R.id.playlistText))
        val nbRoundsTextView = Espresso.onView(withId(R.id.nbRoundsText))
        val hostNameTextView = Espresso.onView(withId(R.id.hostNameText))
        val withHintTextView = Espresso.onView(withId(R.id.withHintText))
        val privateTextView  = Espresso.onView(withId(R.id.privateText))
        val launchGameButton = Espresso.onView(withId(R.id.launchGameButton))

        val testIntent = Intent(ctx, WorkingLobbyActivity::class.java).apply {
            putExtra(GAME_UID_KEY, testUID)
            putExtra(GAME_NAME_KEY, testName)
            putExtra(GAME_PLAYLIST_KEY, Json.encodeToString(testPlaylist))
            putExtra(GAME_NB_ROUNDS_KEY, testNbRounds)
            putExtra(GAME_HINT_KEY, withHint)
            putExtra(GAME_PRIVACY_KEY, private)
            putExtra(GAME_IS_HOST_KEY, false)
        }

        ActivityScenario.launch<WorkingLobbyActivity>(testIntent).use {
            uuidTextView.check(matches(withText(ctx.getString(R.string.uid_text_format, testUID))))
            hostNameTextView.check(matches(withText(ctx.getString(R.string.host_name_format, ""))))
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

    @Test
    fun lobbyFetchesLyricsTest() {
        val lyrics = "If you feel, little chance, make a stance\n" +
            "Looking for, better days, let me say\n" +
            "Something's wrong, when you can't, let me go\n" +
            "For to long, long, long...\n" +
            "\n" +
            "Momentum owns you\n" +
            "Controlling her too"
        // Test values
        var testHost = User("Test User")

        val testPlaylist = Playlist("Stoner Playlist")
        testPlaylist.addSong(Song("Momentum", "Truckfighters"))
        testHost.addPlaylist(testPlaylist)

        val context: Context = ApplicationProvider.getApplicationContext()
        testHost.save(context)

        val testIntent = Intent(ctx, LyricsGettingWorkingLobbyActivity::class.java)

        ActivityScenario.launch<LyricsGettingWorkingLobbyActivity>(testIntent).use {
            testHost = User.load(context)
            assertTrue(testHost.getPlaylists().any { p -> p.getSongs().any { s -> s.lyrics == lyrics } })
        }
    }
}