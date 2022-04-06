package com.github.fribourgsdp.radio

import android.content.Context
import android.view.View
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matcher
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.nullValue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AddPlaylistActivityTest {
    @get:Rule
    var assPlaylistActivityRule = ActivityScenarioRule(AddPlaylistActivity::class.java)
    private val ctx: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun testSongWithNoName(){
        onView(withId(R.id.addSongToPlaylistArtistName))
            .perform(
                ViewActions.typeText("Sardou")
            )
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistBtn))
            .perform(ViewActions.click())
        onView(withId(R.id.addPlaylistErrorTextView))
            .check(ViewAssertions.matches(
                ViewMatchers.withText(ctx.resources.getText(R.string.name_cannot_be_empty).toString())
            ))
    }
    @Test
    fun testEmptyPlaylist(){
        onView(withId(R.id.newPlaylistName))
            .perform(
                ViewActions.typeText("Sardou playlist")
            )
        closeSoftKeyboard()
        onView(withId(R.id.confirmBtn))
            .perform(ViewActions.click())
        onView(withId(R.id.addPlaylistErrorTextView))
            .check(ViewAssertions.matches(
                ViewMatchers.withText(ctx.resources.getText(R.string.playlist_is_empty).toString())
            ))

    }
    @Test
    fun testPlaylistWithNoName(){
        onView(withId(R.id.addSongToPlaylistArtistName))
            .perform(
                ViewActions.typeText("Despacito")
            )
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistBtn))
            .perform(ViewActions.click())
        onView(withId(R.id.confirmBtn))
            .perform(ViewActions.click())
        onView(withId(R.id.addPlaylistErrorTextView))
            .check(ViewAssertions.matches(
                ViewMatchers.withText(ctx.resources.getText(R.string.playlist_has_no_name).toString())
            ))
    }
    @Test
    fun testAddPlaylist(){
        Intents.init()

        initializeSardouPlaylist()

        onView(withId(R.id.confirmBtn))
            .perform(ViewActions.click())

        Intents.intended(
            allOf(
                hasComponent(UserProfileActivity::class.java.name)
            )
        )

        val user = UserProfileActivity.loadUser(ctx)
        assert(user.getPlaylists().any { p -> p.name == "Sardou playlist" })
        user.getPlaylists().filter { p -> p.name == "Sardou playlist" }.forEach{p ->
            run {
                assert(p.getSongs().any { s -> s.name == "Rouge" && s.artist == "Sardou" })
                assert(p.getSongs().any { s -> s.name == "En Chantant" && s.artist == "Sardou" })
                assert(p.getSongs().any { s -> s.name == "Le France" && s.artist == "Sardou" })
            }
        }

        Intents.release()
    }
    @Test
    fun addAndRemoveSong(){
        Intents.init()

        initializeSardouPlaylist()

        onView(withId(R.id.list_playlist_creation))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(0, ViewActions.click()))

        onView(withId(R.id.list_playlist_creation))
            .check(ViewAssertions.matches(ViewMatchers.hasChildCount(2)))

        Intents.release()
    }

    private fun initializeSardouPlaylist(){
        onView(withId(R.id.newPlaylistName))
            .perform(ViewActions.typeText("Sardou playlist"))
        closeSoftKeyboard()

        onView(withId(R.id.addSongToPlaylistSongName))
            .perform(ViewActions.typeText("Rouge"))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistArtistName))
            .perform(ViewActions.typeText("Sardou"))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistBtn))
            .perform(ViewActions.click())

        onView(withId(R.id.addSongToPlaylistSongName))
            .perform(ViewActions.typeText("En chantant"))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistArtistName))
            .perform(ViewActions.typeText("Sardou"))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistBtn))
            .perform(ViewActions.click())

        onView(withId(R.id.addSongToPlaylistSongName))
            .perform(ViewActions.typeText("Le France"))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistArtistName))
            .perform(ViewActions.typeText("Sardou"))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistBtn))
            .perform(ViewActions.click())
    }
}