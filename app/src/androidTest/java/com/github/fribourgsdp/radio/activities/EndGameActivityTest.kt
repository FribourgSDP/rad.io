package com.github.fribourgsdp.radio.activities

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.fribourgsdp.radio.MainActivity
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.game.EndGameActivity
import com.github.fribourgsdp.radio.game.SCORES_KEY
import com.github.fribourgsdp.radio.utils.CustomMatchers.Companion.atPosition
import org.hamcrest.Matchers
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
        ActivityScenario.launch<EndGameActivity>(intent).use{ _ ->
            Espresso.pressBack()
            Intents.intended(
                Matchers.allOf(
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
        ActivityScenario.launch<EndGameActivity>(testIntent).use {
            // Check that the scores are displayed with the correct data and in the correct order
            Espresso.onView(ViewMatchers.withId(R.id.endScoresRecyclerView))
                .check(
                    ViewAssertions.matches(
                        Matchers.allOf(
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
}