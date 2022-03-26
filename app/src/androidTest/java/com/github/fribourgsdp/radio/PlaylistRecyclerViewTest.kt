package com.github.fribourgsdp.radio

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.fribourgsdp.radio.activities.UserProfileActivity
import com.github.fribourgsdp.radio.backend.music.Genre
import com.github.fribourgsdp.radio.backend.music.Playlist
import com.github.fribourgsdp.radio.backend.music.PlaylistAdapter
import com.github.fribourgsdp.radio.backend.gameplay.User
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class PlaylistRecyclerViewTest {
    @get:Rule
    var activityRule = ActivityScenarioRule(UserProfileActivity::class.java)
    private lateinit var user: User

    @Test
    fun recyclerViewDisplayedTest() {
        Intents.init()
        val firebaseAuth = FirebaseAuth.getInstance()
        val task = Tasks.withTimeout(firebaseAuth.signInWithEmailAndPassword("test@test.com", "test123!!!"),10, TimeUnit.SECONDS)
        Tasks.await(task)
        val context: Context = ApplicationProvider.getApplicationContext()

        val intent: Intent = Intent(context, UserProfileActivity::class.java)

        ActivityScenario.launch<UserProfileActivity>(intent).use { scenario ->

            Espresso.onView(withId(R.id.playlist_recycler_view)).check(matches(isDisplayed()))
        }
        Intents.release()
    }

    @Test
    fun recyclerViewTestPlaylistTitleIsCorrect() {
        Intents.init()
        val firebaseAuth = FirebaseAuth.getInstance()
        val task = Tasks.withTimeout(firebaseAuth.signInWithEmailAndPassword("test@test.com", "test123!!!"),10, TimeUnit.SECONDS)
        Tasks.await(task)
        val context: Context = ApplicationProvider.getApplicationContext()
        val string = "test"
        val user = User(string, 0)
        val playlistTitle = "testTitle"
        val playlist1 = Playlist(playlistTitle, Genre.ROCK)
        user.addPlaylists(setOf(playlist1))
        user.save(context)

        val intent: Intent = Intent(context, UserProfileActivity::class.java)

        ActivityScenario.launch<UserProfileActivity>(intent).use { scenario ->
            Espresso.onView(withId(R.id.playlist_recycler_view)).perform(RecyclerViewActions.actionOnItemAtPosition<PlaylistAdapter.PlaylistViewHolder>(0, click()))
            Espresso.onView(withId(R.id.PlaylistName)).check(matches(withText(playlistTitle)))
        }
        Intents.release()
    }
}