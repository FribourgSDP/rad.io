package com.github.fribourgsdp.radio


import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * ==================NOTE THAT FOR THE TESTS TO USE A MOCK IMPLEMENTATION OF THE VOICE CHANNEL, THE FOLLOWING MUST BE ADDED TO THE
 * ==================INTENT WHEN LAUNCHING THIS ACTIVITY: BOOLEAN EXTRA IS_IN_TEST_MODE = TRUE, LONG EXTRA GAME_UID = WHATEVER_LONG_YOU_WANT
 */


@RunWith(AndroidJUnit4::class)
class GameActivityTest {
    private val ctx: Context = ApplicationProvider.getApplicationContext()

    private val fakeGame = Game.Builder()
        .setHost(User("host"))
        .setPlaylist(Playlist("playlist"))
        .build()


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
    fun singerUpdatedCorrectly() {
        // Test values
        val singerName = "Singer"

        // Init views
        val singerTextView = onView(withId(R.id.singerTextView))

        val testIntent = Intent(ctx, GameActivity::class.java)
        ActivityScenario.launch<GameActivity>(testIntent).use { scenario ->
            scenario.onActivity {
                it.updateSinger(singerName)
            }

            singerTextView.check(matches(withText(ctx.getString(R.string.singing_format, singerName))))
        }
    }

    @Test
    fun roundUpdatedCorrectly() {
        // Test values
        val currentRound = 34L

        // Init views
        val currentRoundTextView = onView(withId(R.id.currentRoundView))

        val testIntent = Intent(ctx, GameActivity::class.java)
        ActivityScenario.launch<GameActivity>(testIntent).use { scenario ->
            scenario.onActivity {
                it.updateRound(currentRound)
            }

            currentRoundTextView.check(matches(withText(ctx.getString(R.string.current_round_format, currentRound))))
        }
    }

    @Test
    fun songDisplayedCorrectly() {
        // Test values
        val songName = "Test song"

        // Init views
        val songTextView = onView(withId(R.id.songTextView))
        val songGuessEditText = onView(withId(R.id.songGuessEditText))
        val songGuessSubmitButton = onView(withId(R.id.songGuessSubmitButton))

        val testIntent = Intent(ctx, GameActivity::class.java).apply {
            putExtra(GAME_IS_HOST_KEY, true)
            putExtra(GAME_KEY, json.encodeToString(fakeGame))
            putExtra(GAME_UID, 37L)
            putExtra(IS_IN_TEST_MODE, true)
        }

        ActivityScenario.launch<GameActivity>(testIntent).use { scenario ->
            scenario.onActivity {
                it.displaySong(songName)
            }

            songGuessEditText.check(matches(not(isDisplayed())))
            songGuessSubmitButton.check(matches(not(isDisplayed())))
            songTextView.check(matches(isDisplayed()))
            songTextView.check(matches(withText(songName)))
        }
    }

    @Test
    fun guessInputDisplayedCorrectly() {
        // Init views
        val songTextView = onView(withId(R.id.songTextView))
        val songGuessEditText = onView(withId(R.id.songGuessEditText))
        val songGuessSubmitButton = onView(withId(R.id.songGuessSubmitButton))

        val testIntent = Intent(ctx, GameActivity::class.java)
        ActivityScenario.launch<GameActivity>(testIntent).use { scenario ->
            scenario.onActivity {
                it.displayGuessInput()
            }

            songGuessEditText.check(matches(isDisplayed()))
            songGuessSubmitButton.check(matches(isDisplayed()))
            songTextView.check(matches(not(isDisplayed())))
        }
    }

    @Test
    fun errorDisplayedCorrectly() {
        // Test values
        val errorMessage = "This is an error message"

        // Init views
        val errorOrFailureTextView = onView(withId(R.id.errorOrFailureTextView))

        val testIntent = Intent(ctx, GameActivity::class.java)
        ActivityScenario.launch<GameActivity>(testIntent).use { scenario ->
            scenario.onActivity {
                it.displayError(errorMessage)
            }

            errorOrFailureTextView.check(matches(withText(errorMessage)))
            errorOrFailureTextView.check(matches(isDisplayed()))
        }
    }

    @Test
    fun errorHiddenCorrectly() {
        // Init views
        val errorOrFailureTextView = onView(withId(R.id.errorOrFailureTextView))

        val testIntent = Intent(ctx, GameActivity::class.java)
        ActivityScenario.launch<GameActivity>(testIntent).use { scenario ->
            scenario.onActivity {
                it.hideError()
            }
            errorOrFailureTextView.check(matches(not(isDisplayed())))
        }
    }

    @Test
    fun songPickerDisplayedOnChoose() {
        val listSongs = arrayListOf("Song0", "Song1", "Song2")

        val testIntent = Intent(ctx, GameActivity::class.java)
        ActivityScenario.launch<GameActivity>(testIntent).use { scenario ->
            scenario.onActivity {
                it.chooseSong(listSongs,
                    object: GameView.OnPickListener {
                        override fun onPick(song: String) {
                            // DO NOTHING
                        }
                    }
                )
            }

            onView(withText(R.string.pick_a_song)) // Look for the dialog => use its title
                .inRoot(isDialog()) // check that it's indeed in a dialog
                .check(matches(isDisplayed()));
        }
    }
}