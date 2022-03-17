package com.github.fribourgsdp.radio

import android.content.Context
import android.view.KeyEvent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.allOf
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
    var mainActivityRule = ActivityScenarioRule(GameSettingsActivity::class.java)

    private val ctx: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun intentWorksWithCorrectSettings() {
        Intents.init()

        // Test values
        val testName = "Hello World!"
        val testPlaylist = "Rap Playlist"
        val testNbRounds = 20
        val withHint = true
        val private = true


        onView(withId(R.id.nameInput))
            .perform(ViewActions.typeText(testName))

        closeSoftKeyboard()

        onView(withId(R.id.playlistSearchView))
            .perform(
                ViewActions.click(),
                ViewActions.typeText(testPlaylist),
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
                hasExtraWithKey(GAME_HOST_KEY),
                hasExtra(GAME_NAME_KEY, testName),
                hasExtra(GAME_PLAYLIST_KEY, testPlaylist),
                hasExtra(GAME_NB_ROUNDS_KEY, testNbRounds),
                hasExtra(GAME_HINT_KEY, withHint),
                hasExtra(GAME_PRIVACY_KEY, private)
            )
        )

        Intents.release()
    }


    @Test
    fun intentWorksWithDefaultSettings() {
        Intents.init()

        // Test values
        val testPlaylist = "Rap Playlist"

        onView(withId(R.id.playlistSearchView))
            .perform(
                ViewActions.click(),
                ViewActions.typeText(testPlaylist),
                ViewActions.pressKey(KeyEvent.KEYCODE_ENTER)
            )

        closeSoftKeyboard()

        onView(withId(R.id.startButton))
            .perform(ViewActions.click())

        Intents.intended(
            allOf(
                hasComponent(LobbyActivity::class.java.name),
                hasExtraWithKey(GAME_HOST_KEY),
                hasExtra(GAME_NAME_KEY, ctx.getString(R.string.default_game_name)),
                hasExtra(GAME_PLAYLIST_KEY, testPlaylist),
                hasExtra(GAME_NB_ROUNDS_KEY, ctx.getString(R.string.default_game_nb_rounds).toInt()),
                hasExtra(GAME_HINT_KEY, false),
                hasExtra(GAME_PRIVACY_KEY, false),
                toPackage("com.github.fribourgsdp.radio")
            )
        )

        Intents.release()
    }
}