package com.github.fribourgsdp.radio.activities

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.fribourgsdp.radio.MainActivity
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.game.EndGameActivity
import com.github.fribourgsdp.radio.game.GAME_CRASH_KEY
import com.github.fribourgsdp.radio.game.SCORES_KEY
import com.github.fribourgsdp.radio.utils.CustomMatchers.Companion.atPosition
import org.hamcrest.Matchers.allOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * End Game Activity Tests
 *
 */
@RunWith(AndroidJUnit4::class)
class EndGameActivityTest{
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
    fun onBackPressedGoesToMainMenu() {
        val intent = Intent(ctx, EndGameActivity::class.java)
        launch<EndGameActivity>(intent).use{ _ ->
            pressBack()
            Intents.intended(
                allOf(
                    IntentMatchers.hasComponent(MainActivity::class.java.name)
                )
            )
        }
    }

    @Test
    fun finalScoresDisplayedCorrectly() {
        val scores = arrayListOf(
            Pair("singer0", 85L),
            Pair("singer1", 70L),
            Pair("singer2", 100L)
        )

        val testIntent = Intent(ctx, EndGameActivity::class.java).apply {
            putExtra(SCORES_KEY, scores)
        }
        launch<EndGameActivity>(testIntent).use {
            // Check that the scores are displayed with the correct data and in the correct order
            onView(withId(R.id.endScoresRecyclerView))
                .check(
                    matches(
                        allOf(
                            atPosition(0, R.id.nameScoreTextView, ViewMatchers.withText("singer2")),
                            atPosition(0, R.id.scoreTextView, ViewMatchers.withText("100")),
                            atPosition(1, R.id.nameScoreTextView, ViewMatchers.withText("singer0")),
                            atPosition(1, R.id.scoreTextView, ViewMatchers.withText("85")),
                            atPosition(2, R.id.nameScoreTextView, ViewMatchers.withText("singer1")),
                            atPosition(2, R.id.scoreTextView, ViewMatchers.withText("70"))
                        )
                    )
                )
        }
    }

    @Test
    fun congratsMessagedOnCorrectEnd() {
        val testIntent = Intent(ctx, EndGameActivity::class.java).apply {
            putExtra(GAME_CRASH_KEY, false)
        }

        launch<EndGameActivity>(testIntent).use {
            // Check that the scores are displayed with the correct data and in the correct order
            onView(withId(R.id.gameOverTextView))
                .check(matches(withText(R.string.congrats)))
        }
    }

    @Test
    fun crashMessagedOnFailure() {
        val testIntent = Intent(ctx, EndGameActivity::class.java).apply {
            putExtra(GAME_CRASH_KEY, true)
        }

        launch<EndGameActivity>(testIntent).use {
            // Check that the scores are displayed with the correct data and in the correct order
            onView(withId(R.id.gameOverTextView))
                .check(matches(withText(R.string.game_crashed)))
        }
    }
}