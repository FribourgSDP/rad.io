package com.github.fribourgsdp.radio.activities

import android.content.Context
import android.view.KeyEvent
import android.view.View
import android.widget.TextView
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.fribourgsdp.radio.MainActivity
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.game.prep.*
import com.github.fribourgsdp.radio.mockimplementations.MockGameSettingsActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hamcrest.Matcher
import org.hamcrest.Matchers
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
    fun timerDialogAppearsCorrectlyWhenPressingTimerButton() {
        onView(withId(R.id.chooseTimeButton))
            .perform(ViewActions.click())
    }

    @Test
    fun timerDialogHasDefaultValue45Seconds() {
        onView(withId(R.id.timerTextView))
            .check(ViewAssertions.matches(
                ViewMatchers.withText("45s")
            ))
    }

    @Test
    fun intentWorksWithCorrectSettings() {

        // Test values
        val testName = "Hello World!"
        val testPlaylist = Playlist("Rap Playlist")
        val testNbRounds = 20
        val withHint = true
        val private = true

        onView(withId(R.id.chooseTimeButton))
            .perform(ViewActions.click())
        onView(withText("OK"))
            .check(matches(isDisplayed()))
            .perform(ViewActions.click())


        val chosenTime = getText(withId(R.id.timerTextView))
        val chosenTimeInSeconds = chosenTime?.take((chosenTime!!.length - 1))?.toLong()

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
                        hasExtra(GAME_PRIVACY_KEY, private),
                        hasExtra(GAME_DURATION_KEY, chosenTimeInSeconds)
                    )
                )
    }

    fun getText(matcher: Matcher<View?>?): String? {
        val stringHolder = arrayOf<String?>(null)
        onView(matcher).perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(TextView::class.java)
            }

            override fun getDescription(): String {
                return "getting text from a TextView"
            }

            override fun perform(uiController: UiController?, view: View) {
                val tv = view as TextView //Save, because of check in getConstraints()
                stringHolder[0] = tv.text.toString()
            }
        })
        return stringHolder[0]
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
                hasExtra(GAME_DURATION_KEY, DEFAULT_GAME_DURATION),
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

    @Test
    fun pressBackReturnsToMainActivity() {
        Espresso.pressBack()
        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(MainActivity::class.java.name)
            )
        )
    }

}