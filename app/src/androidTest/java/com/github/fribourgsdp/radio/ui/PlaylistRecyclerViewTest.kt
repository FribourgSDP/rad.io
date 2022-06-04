package com.github.fribourgsdp.radio.ui

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
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.mockimplementations.MockUserProfileActivity
import com.github.fribourgsdp.radio.utils.playListName
import com.github.fribourgsdp.radio.utils.songName
import com.github.fribourgsdp.radio.util.ViewHolder
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class PlaylistRecyclerViewTest {

    @Before
    fun initIntent() {
        Intents.init()
    }

    @After
    fun releaseIntent() {
        Intents.release()
    }

    @Test
    fun recyclerViewDisplayedTest() {
        val firebaseAuth = FirebaseAuth.getInstance()
        val task = Tasks.withTimeout(firebaseAuth.signInWithEmailAndPassword("test@test.com", "test123!!!"),10, TimeUnit.SECONDS)
        Tasks.await(task)

        val context : Context = ApplicationProvider.getApplicationContext()

        val intent = Intent(context, MockUserProfileActivity::class.java)

        ActivityScenario.launch<MockUserProfileActivity>(intent).use {
            Espresso.onView(withId(R.id.playlist_recycler_view)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun recyclerViewTestPlaylistTitleIsCorrect() {
        val firebaseAuth = FirebaseAuth.getInstance()
        val task = Tasks.withTimeout(
            firebaseAuth.signInWithEmailAndPassword(
                "test@test.com",
                "test123!!!"
            ), 10, TimeUnit.SECONDS
        )
        Tasks.await(task)

        val context: Context = ApplicationProvider.getApplicationContext()

        val intent = Intent(context, MockUserProfileActivity::class.java)

        ActivityScenario.launch<MockUserProfileActivity>(intent).use {
            Espresso.onView(withId(R.id.playlist_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(0, click()))
            Espresso.onView(withId(R.id.PlaylistName)).check(matches(withText(playListName)))
        }
    }

    @Test
    fun recyclerViewTestFullDepth() {
        val firebaseAuth = FirebaseAuth.getInstance()
        val task = Tasks.withTimeout(
            firebaseAuth.signInWithEmailAndPassword(
                "test@test.com",
                "test123!!!"
            ), 10, TimeUnit.SECONDS
        )
        Tasks.await(task)

        val context: Context = ApplicationProvider.getApplicationContext()

        val intent = Intent(context, MockUserProfileActivity::class.java)

        ActivityScenario.launch<MockUserProfileActivity>(intent).use {
            Espresso.onView(withId(R.id.playlist_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(0, click()))
            Espresso.onView(withId(R.id.PlaylistName)).check(matches(withText(playListName)))
            Espresso.onView(withId(R.id.SongRecyclerView)).check(matches(isDisplayed()))
            Espresso.onView(withId(R.id.SongRecyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(0, click()))
            Espresso.onView(withId(R.id.SongName))
                .check(matches(withText(songName)))
        }
    }

    @Test
    fun recyclerViewTestDeleteUpdatesInstantly() {
        val firebaseAuth = FirebaseAuth.getInstance()
        val task = Tasks.withTimeout(
            firebaseAuth.signInWithEmailAndPassword(
                "test@test.com",
                "test123!!!"
            ), 10, TimeUnit.SECONDS
        )
        Tasks.await(task)

        val context: Context = ApplicationProvider.getApplicationContext()

        val intent = Intent(context, MockUserProfileActivity::class.java)

        ActivityScenario.launch<MockUserProfileActivity>(intent).use {
            Espresso.onView(withId(R.id.playlist_recycler_view))
                .check(matches(hasChildCount(1)))
            Espresso.onView(withId(R.id.playlist_recycler_view))
                .perform(RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(0, click()))
            Espresso.onView(withId(R.id.deleteButton))
                .perform(click())
            Espresso.onView(withId(R.id.playlist_recycler_view))
                .check(matches(hasChildCount(0)))
        }
    }
}