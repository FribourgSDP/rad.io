package com.github.fribourgsdp.radio

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


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

        val testIntent = Intent(ctx, GameActivity::class.java).apply {
            putExtra(GAME_IS_HOST_KEY, true)
            putExtra(GAME_KEY, json.encodeToString(fakeGame))
        }

        ActivityScenario.launch<GameActivity>(testIntent).use { scenario ->
            scenario.onActivity {
                it.displaySong(songName)
            }

            songGuessEditText.check(matches(not(isDisplayed())))
            songTextView.check(matches(isDisplayed()))
            songTextView.check(matches(withText(songName)))
        }
    }

    @Test
    fun guessInputDisplayedCorrectly() {
        // Init views
        val songTextView = onView(withId(R.id.songTextView))
        val songGuessEditText = onView(withId(R.id.songGuessEditText))

        val testIntent = Intent(ctx, GameActivity::class.java)
        ActivityScenario.launch<GameActivity>(testIntent).use { scenario ->
            scenario.onActivity {
                it.displayGuessInput()
            }

            songGuessEditText.check(matches(isDisplayed()))
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
    fun songPickerWithCorrectSongs() {
        val listSongs = listOf("Wesh alors", "Mamma Mia!", "La Bohème")

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

            val button0 = onView(withId(R.id.songPick0))

            button0.check(matches(withText(listSongs[0])))
                .check(matches(isDisplayed()))

            onView(withId(R.id.songPick1))
                .check(matches(withText(listSongs[1])))
                .check(matches(isDisplayed()))

            onView(withId(R.id.songPick2))
                .check(matches(withText(listSongs[2])))
                .check(matches(isDisplayed()))

            // Click to dismiss dialog
            button0.perform(ViewActions.click())

        }
    }

    @Test
    fun songPickerReturnsCorrectPick() {
        val listSongs = listOf("Wesh alors", "Mamma Mia!", "La Bohème")
        var testPick = ""

        val button0 = onView(withId(R.id.songPick0))

        val testIntent = Intent(ctx, GameActivity::class.java)
        ActivityScenario.launch<GameActivity>(testIntent).use { scenario ->
            scenario.onActivity {
                it.chooseSong(listSongs,
                    object: GameView.OnPickListener {
                        override fun onPick(song: String) {
                            testPick = song
                        }

                    }
                )
            }

            button0.perform(ViewActions.click())

            assertEquals(listSongs[0], testPick)
        }
    }

    @Test
    fun hideDialogButtonOnLessSongs() {
        val listSongs = listOf("Wesh alors", "Mamma Mia!")

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

            onView(withId(R.id.songPick2))
                .check(matches(not(isDisplayed())))

            // Click to dismiss dialog
            onView(withId(R.id.songPick0))
                .perform(ViewActions.click())
        }
    }
}