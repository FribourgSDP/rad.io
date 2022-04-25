package com.github.fribourgsdp.radio

import android.content.Context
import android.view.KeyEvent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.fribourgsdp.radio.mockimplementations.MockGameSettingsActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Game Settings Activity Tests
 *
 */
@RunWith(AndroidJUnit4::class)
class GameSettingsActivityTest {
    @get:Rule
    var gameSettingsActivityRule = ActivityScenarioRule(MockGameSettingsActivity::class.java)

    private val ctx: Context = ApplicationProvider.getApplicationContext()


    private val json = Json {
        allowStructuredMapKeys = true
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
    fun intentWorksWithCorrectSettings() {

        // Test values
        val testName = "Hello World!"
        val testPlaylist = Playlist("Rap Playlist")
        val testNbRounds = 20
        val withHint = true
        val private = true


        onView(withId(R.id.nameInput))
            .perform(ViewActions.typeText(testName))

        closeSoftKeyboard()

        onView(withId(R.id.playlistSearchView))
            .perform(
                ViewActions.click(),
                ViewActions.typeText(testPlaylist.name),
                ViewActions.pressKey(KeyEvent.KEYCODE_ENTER)
            )

        closeSoftKeyboard()

        onView(withId(R.id.nbRoundsInput))
            .perform(ViewActions.typeText(testNbRounds.toString()))

        closeSoftKeyboard()

        if (withHint) {
            onView(withId(R.id.hintCheckBox))
                .perform(ViewActions.click())
        }


        if (private) {
            onView(withId(R.id.privacyCheckBox))
                .perform(ViewActions.click())
        }

        onView(withId(R.id.startButton))
            .perform(ViewActions.click())

        Intents.intended(
            allOf(
                toPackage("com.github.fribourgsdp.radio"),
                hasComponent(LobbyActivity::class.java.name),
                hasExtra(GAME_NAME_KEY, testName),
                hasExtra(GAME_PLAYLIST_KEY, Json.encodeToString(testPlaylist)),
                hasExtra(GAME_NB_ROUNDS_KEY, testNbRounds),
                hasExtra(GAME_HINT_KEY, withHint),
                hasExtra(GAME_PRIVACY_KEY, private)
            )
        )
    }


    @Test
    fun intentWorksWithDefaultSettings() {

        // Test values
        val testPlaylist = Playlist("Rap Playlist")

        onView(withId(R.id.playlistSearchView))
            .perform(
                ViewActions.click(),
                ViewActions.typeText(testPlaylist.name),
                ViewActions.pressKey(KeyEvent.KEYCODE_ENTER)
            )

        closeSoftKeyboard()

        onView(withId(R.id.startButton))
            .perform(ViewActions.click())

        Intents.intended(
            allOf(
                hasComponent(LobbyActivity::class.java.name),
                hasExtra(GAME_NAME_KEY, ctx.getString(R.string.default_game_name)),
                hasExtra(GAME_PLAYLIST_KEY, Json.encodeToString(testPlaylist)),
                hasExtra(GAME_NB_ROUNDS_KEY, ctx.getString(R.string.default_game_nb_rounds).toInt()),
                hasExtra(GAME_HINT_KEY, false),
                hasExtra(GAME_PRIVACY_KEY, false),
                hasExtra(GAME_IS_HOST_KEY, true),
                toPackage("com.github.fribourgsdp.radio")
            )
        )

    }

    @Test
    fun startButtonDisabledOnNoPlaylist() {
        onView(withId(R.id.startButton))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.isNotEnabled()
                )
            )
    }

    @Test
    fun startButtonDisabledOnBadPlaylist() {
        onView(withId(R.id.playlistSearchView))
            .perform(
                ViewActions.click(),
                ViewActions.typeText("Not a Playlist"),
                ViewActions.pressKey(KeyEvent.KEYCODE_ENTER)
            )

        closeSoftKeyboard()

        onView(withId(R.id.startButton))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.isNotEnabled()
                )
            )
    }

    @Test
    fun startButtonDisabledWhenRewritingPlaylist() {
        // First type correct playlist
        onView(withId(R.id.playlistSearchView))
            .perform(
                ViewActions.click(),
                ViewActions.typeText("Rap Playlist"),
                ViewActions.pressKey(KeyEvent.KEYCODE_ENTER)
            )

        closeSoftKeyboard()

        onView(withId(R.id.startButton))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.isEnabled()
                )
            )

        // Then delete on character
        onView(withId(R.id.playlistSearchView))
            .perform(
                ViewActions.click(),
                ViewActions.pressKey(KeyEvent.KEYCODE_DEL),
                ViewActions.pressKey(KeyEvent.KEYCODE_ENTER)
            )

        closeSoftKeyboard()

        onView(withId(R.id.startButton))
            .check(
                ViewAssertions.matches(
                    ViewMatchers.isNotEnabled()
                )
            )
    }


    @Test
    fun errorTextOnBadPlaylistName() {
        val fakeName = "Not a Playlist"

        onView(withId(R.id.playlistSearchView))
            .perform(
                ViewActions.click(),
                ViewActions.typeText(fakeName),
                ViewActions.pressKey(KeyEvent.KEYCODE_ENTER)
            )

        closeSoftKeyboard()

        val errorTextView = onView(withId(R.id.playlistSearchError))

        errorTextView.check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        errorTextView.check(
            ViewAssertions.matches(
                ViewMatchers.withText(ctx.getString(R.string.playlist_error_format, fakeName))
            )
        )

    }
}