package com.github.fribourgsdp.radio.activities

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
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.game.prep.GAME_HOST_KEY
import com.github.fribourgsdp.radio.game.prep.GAME_IS_HOST_KEY
import com.github.fribourgsdp.radio.game.prep.GAME_PLAYLIST_KEY
import com.github.fribourgsdp.radio.mockimplementations.BuggyLobbyActivity
import com.github.fribourgsdp.radio.utils.testPlaylist1
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Lobby Activity Tests with buggy database
 * NB: Here we test on [BuggyLobbyActivity], that uses a [BuggyDatabase],
 * so that the tests don't depend on firebase.
 *
 */
@RunWith(AndroidJUnit4::class)
class BuggyLobbyActivityTest {
    private val testHostUser = "host"
    @get:Rule
    var lobbyActivityRule = ActivityScenarioRule(BuggyLobbyActivity::class.java)

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
    fun lobbyDisplayErrorWhenDBFails() {
        // Init views
        val uuidTextView = Espresso.onView(withId(R.id.uuidText))

        val testIntent = Intent(ctx, BuggyLobbyActivity::class.java).apply {
            putExtra(GAME_IS_HOST_KEY, true)
            putExtra(GAME_HOST_KEY, Json.encodeToString(User(testHostUser)))
            putExtra(GAME_PLAYLIST_KEY, Json.encodeToString(Playlist(testPlaylist1)))
        }

        ActivityScenario.launch<BuggyLobbyActivity>(testIntent).use {
            uuidTextView.check(matches(withText(ctx.getString(R.string.uid_error))))
        }

    }
    
}