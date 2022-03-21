package com.github.fribourgsdp.radio

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Lobby Activity Tests
 *
 */
@RunWith(AndroidJUnit4::class)
class LobbyActivityTest {
    @get:Rule
    var lobbyActivityRule = ActivityScenarioRule(LobbyActivity::class.java)

    private val ctx: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun lobbyDisplayCorrectInfos() {
        Intents.init()

        // Test values
        val testHostName = "The Boss"
        val testName = "Hello World!"
        val testPlaylist = "Rap Playlist"
        val testNbRounds = 20
        val withHint = true
        val private = true

        // Init views
        val uuidTextView     = Espresso.onView(withId(R.id.uuidText))
        val hostNameTextView = Espresso.onView(withId(R.id.hostNameText))
        val gameNameTextView = Espresso.onView(withId(R.id.gameNameText))
        val playlistTextView = Espresso.onView(withId(R.id.playlistText))
        val nbRoundsTextView = Espresso.onView(withId(R.id.nbRoundsText))
        val withHintTextView = Espresso.onView(withId(R.id.withHintText))
        val privateTextView  = Espresso.onView(withId(R.id.privateText))

        val testIntent = Intent(ctx, LobbyActivity::class.java).apply {
            putExtra(GAME_HOST_KEY, testHostName)
            putExtra(GAME_NAME_KEY, testName)
            putExtra(GAME_PLAYLIST_KEY, testPlaylist)
            putExtra(GAME_NB_ROUNDS_KEY, testNbRounds)
            putExtra(GAME_HINT_KEY, withHint)
            putExtra(GAME_PRIVACY_KEY, private)

        }

        ActivityScenario.launch<LobbyActivity>(testIntent).use {
            uuidTextView.check(matches(withText(ctx.getString(R.string.uuid_text_format, 1))))
            hostNameTextView.check(matches(withText(ctx.getString(R.string.host_name_format, testHostName))))
            gameNameTextView.check(matches(withText(ctx.getString(R.string.game_name_format, testName))))
            playlistTextView.check(matches(withText(ctx.getString(R.string.playlist_format, testPlaylist))))
            nbRoundsTextView.check(matches(withText(ctx.getString(R.string.number_of_rounds_format, testNbRounds))))
            withHintTextView.check(matches(withText(ctx.getString(R.string.hints_enabled_format, withHint))))
            privateTextView.check(matches(withText(ctx.getString(R.string.private_format, private))))
        }

        Intents.release()
    }

}