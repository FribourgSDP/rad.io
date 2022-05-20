package com.github.fribourgsdp.radio.activities


import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.external.musixmatch.MusixmatchLyricsGetter
import com.github.fribourgsdp.radio.game.*
import com.github.fribourgsdp.radio.game.handler.NO_SINGER
import com.github.fribourgsdp.radio.game.prep.GAME_IS_HOST_KEY
import com.github.fribourgsdp.radio.game.prep.GAME_IS_NO_SING_MODE
import com.github.fribourgsdp.radio.game.prep.GAME_KEY
import com.github.fribourgsdp.radio.mockimplementations.MockGameActivity
import com.github.fribourgsdp.radio.utils.CustomMatchers.Companion.atPosition
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GameActivityTest {
    private val ctx: Context = ApplicationProvider.getApplicationContext()

    private val fakeGame = Game.Builder()
        .setHost(User("host"))
        .setPlaylist(Playlist("playlist"))
        .build()

    @Before
    fun initIntent() {
        Intents.init()
    }

    @After
    fun releaseIntent() {
        Intents.release()
    }

    @Test
    fun onBackPressedHostThenCancelStays() {
        val testIntent = Intent(ctx, MockGameActivity::class.java)
            .putExtra(GAME_IS_HOST_KEY, true)
            .putExtra(GAME_KEY, Json.encodeToString(fakeGame))
        ActivityScenario.launch<MockGameActivity>(testIntent).use { _ ->
            Espresso.pressBack()
            onView(withId(R.id.cancelQuitGameOrLobby))
                .inRoot(isDialog())
                .perform(ViewActions.click())
        }
    }

    @Test
    fun onBackPressedNonHostThenReturnStays() {
        val testIntent = Intent(ctx, MockGameActivity::class.java)
            .putExtra(GAME_IS_HOST_KEY, false)
        ActivityScenario.launch<MockGameActivity>(testIntent).use {_ ->
            Espresso.pressBack()
            onView(withId(R.id.cancelQuitGame))
                .inRoot(isDialog())
                .perform(ViewActions.click())
        }
    }

    @Test
    fun onBackPressedHostThenContinueGoesToMain() {
        val testIntent = Intent(ctx, MockGameActivity::class.java)
            .putExtra(GAME_IS_HOST_KEY, true)
            .putExtra(GAME_KEY, Json.encodeToString(fakeGame))
        ActivityScenario.launch<MockGameActivity>(testIntent).use { _ ->
            Espresso.pressBack()
            Espresso.onView(withId(R.id.validateQuitGameOrLobby))
                .inRoot(isDialog())
                .perform(ViewActions.click())
        }
    }

    @Test
    fun singerUpdatedCorrectly() {
        // Test values
        val singerName = "Singer"

        // Init views
        val singerTextView = onView(withId(R.id.singerTextView))

        val testIntent = Intent(ctx, MockGameActivity::class.java)
        ActivityScenario.launch<MockGameActivity>(testIntent).use { scenario ->
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

        val testIntent = Intent(ctx, MockGameActivity::class.java)
        ActivityScenario.launch<MockGameActivity>(testIntent).use { scenario ->
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

        val testIntent = Intent(ctx, MockGameActivity::class.java).apply {
            putExtra(GAME_IS_HOST_KEY, true)
            putExtra(GAME_KEY, Json.encodeToString(fakeGame))
        }

        ActivityScenario.launch<MockGameActivity>(testIntent).use { scenario ->
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

        val testIntent = Intent(ctx, MockGameActivity::class.java)
        ActivityScenario.launch<MockGameActivity>(testIntent).use { scenario ->
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

        val testIntent = Intent(ctx, MockGameActivity::class.java)
        ActivityScenario.launch<MockGameActivity>(testIntent).use { scenario ->
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

        val testIntent = Intent(ctx, MockGameActivity::class.java)
        ActivityScenario.launch<MockGameActivity>(testIntent).use { scenario ->
            scenario.onActivity {
                it.hideError()
            }
            errorOrFailureTextView.check(matches(not(isDisplayed())))
        }
    }

    @Test
    fun songPickerDisplayedOnChoose() {
        val listSongs = arrayListOf("Song0", "Song1", "Song2")

        val testIntent = Intent(ctx, MockGameActivity::class.java)
        ActivityScenario.launch<MockGameActivity>(testIntent).use { scenario ->
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

    @Test
    fun scoresDisplayedCorrectly() {
        val scores = hashMapOf(
            "singer0" to 85L,
            "singer1" to 70L,
            "singer2" to 100L
        )

        val testIntent = Intent(ctx, GameActivity::class.java)
        ActivityScenario.launch<GameActivity>(testIntent).use { scenario ->
            scenario.onActivity {
                it.displayPlayerScores(scores)
            }

            // Check that the scores are displayed with the correct data and in the correct order
            onView(withId(R.id.scoresRecyclerView))
                .check(matches(allOf(
                    atPosition(0, R.id.nameScoreTextView, withText("singer2")),
                    atPosition(0, R.id.scoreTextView, withText("100")),
                    atPosition(1, R.id.nameScoreTextView, withText("singer0")),
                    atPosition(1, R.id.scoreTextView, withText("85")),
                    atPosition(2, R.id.nameScoreTextView, withText("singer1")),
                    atPosition(2, R.id.scoreTextView, withText("70"))
                )))
        }
    }

    @Test
    fun testDisplayLyrics() {
        val testIntent = Intent(ctx, GameActivity::class.java)
        ActivityScenario.launch<GameActivity>(testIntent).use { scenario ->
            scenario.onActivity {
                it.updateLyrics("Lorem ipsum, dolor sit amet")
                // Display the song to see if the button is displayed
                it.displaySong("Lorem ipsum")
            }

            onView(withId(R.id.showLyricsButton))
                .check(matches(isDisplayed()))

        }
    }

    @Test
    fun testDisplayLyricsNotShownWhenEmpty() {
        val testIntent = Intent(ctx, GameActivity::class.java)
        ActivityScenario.launch<GameActivity>(testIntent).use { scenario ->
            scenario.onActivity {
                it.updateLyrics("")
                // Display the song to see if the button is displayed
                it.displaySong("")
            }

            onView(withId(R.id.showLyricsButton))
                .check(matches(not(isDisplayed())))

        }
    }

    @Test
    fun testDisplayLyricsNotShownWhenUnavailable() {
        val testIntent = Intent(ctx, GameActivity::class.java)
        ActivityScenario.launch<GameActivity>(testIntent).use { scenario ->
            scenario.onActivity {
                it.updateLyrics(MusixmatchLyricsGetter.LYRICS_NOT_FOUND_PLACEHOLDER)
                // Display the song to see if the button is displayed
                it.displaySong("")
            }

            onView(withId(R.id.showLyricsButton))
                .check(matches(not(isDisplayed())))

        }
    }

    @Test
    fun goToEndGameActivityOnGameOver() {
        val scores = hashMapOf(
            "singer0" to 85L,
            "singer1" to 70L,
            "singer2" to 100L
        )

        val testIntent = Intent(ctx, GameActivity::class.java)
        ActivityScenario.launch<GameActivity>(testIntent).use { scenario ->
            scenario.onActivity {
                it.gameOver(scores)
            }

            Intents.intended(
                allOf(
                    IntentMatchers.hasComponent(EndGameActivity::class.java.name),
                    IntentMatchers.hasExtra(SCORES_KEY, ArrayList(scores.toList())),
                    IntentMatchers.toPackage("com.github.fribourgsdp.radio")
                )
            )
        }
    }

    @Test
    fun updateSingerInNoSingModeDisplaysNeutralText(){
        val testIntent = Intent(ctx, GameActivity::class.java)
        testIntent.putExtra(GAME_IS_NO_SING_MODE, true)
        ActivityScenario.launch<GameActivity>(testIntent).use { scenario ->
            scenario.onActivity {
                it.updateSinger(NO_SINGER)
                onView(withId(R.id.singerTextView)).check(matches(withText(ctx.getString(R.string.listen))))
            }
        }
    }
}