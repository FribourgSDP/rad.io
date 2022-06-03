package com.github.fribourgsdp.radio.activities

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.data.view.UserProfileActivity
import com.github.fribourgsdp.radio.mockimplementations.MockAddPlaylistActivity
import com.github.fribourgsdp.radio.mockimplementations.MockAddPlaylistActivityNoConnection
import com.google.android.gms.tasks.Tasks
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddPlaylistActivityWithoutConnectionTest {

    @get:Rule
    var addPlaylistActivityRule = ActivityScenarioRule(MockAddPlaylistActivityNoConnection::class.java)

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
    fun addPlaylistWithoutConnectionSavesOnlyOnline(){
        initializeSardouPlaylist()
        Espresso.onView(ViewMatchers.withId(R.id.confirmBtn))
            .perform(ViewActions.click())

        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(UserProfileActivity::class.java.name)
            )
        )

        val user = Tasks.await(User.loadOrDefault(ctx))
        assert(user.getPlaylists().any { p -> p.name == testPlaylist })
        user.getPlaylists().filter { p -> p.name == testPlaylist}.forEach{p ->
            run {
                assert(p.getSongs().any { s -> s.name == testSong1 && s.artist == testArtist })
                assert(p.getSongs().any { s -> s.name == testSong2 && s.artist == testArtist })
                assert(p.getSongs().any { s -> s.name == testSong3 && s.artist == testArtist })
            }
        }

    }

    private fun initializeSardouPlaylist() {
        Espresso.onView(ViewMatchers.withId(R.id.newPlaylistName))
            .perform(ViewActions.typeText(testPlaylist))
        Espresso.closeSoftKeyboard()

        Espresso.onView(ViewMatchers.withId(R.id.addSongToPlaylistSongName))
            .perform(ViewActions.typeText(testSong1))
        Espresso.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withId(R.id.addSongToPlaylistArtistName))
            .perform(ViewActions.typeText(testArtist))
        Espresso.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withId(R.id.addSongToPlaylistBtn))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.addSongToPlaylistSongName))
            .perform(ViewActions.typeText(testSong2))
        Espresso.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withId(R.id.addSongToPlaylistArtistName))
            .perform(ViewActions.typeText(testArtist))
        Espresso.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withId(R.id.addSongToPlaylistBtn))
            .perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.addSongToPlaylistSongName))
            .perform(ViewActions.typeText(testSong3))
        Espresso.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withId(R.id.addSongToPlaylistArtistName))
            .perform(ViewActions.typeText(testArtist))
        Espresso.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withId(R.id.addSongToPlaylistBtn))
            .perform(ViewActions.click())
    }
}