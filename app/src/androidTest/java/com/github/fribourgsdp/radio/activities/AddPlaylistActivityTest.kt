package com.github.fribourgsdp.radio.activities

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.data.Genre
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.data.view.UserProfileActivity
import com.github.fribourgsdp.radio.mockimplementations.MockAddPlaylistActivity
import com.github.fribourgsdp.radio.util.ViewHolder
import com.github.fribourgsdp.radio.utils.*
import com.google.android.gms.tasks.Tasks
import org.hamcrest.Matchers.*
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddPlaylistActivityTest {

    @get:Rule
    var addPlaylistActivityRule = ActivityScenarioRule(MockAddPlaylistActivity::class.java)
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
    fun testSongWithNoName(){
        onView(withId(R.id.addSongToPlaylistArtistName))
            .perform(
                ViewActions.typeText(testArtist)
            )
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistBtn))
            .perform(click())
        onView(withId(R.id.addPlaylistErrorTextView))
            .check(
                matches(
                withText(ctx.resources.getText(R.string.name_cannot_be_empty).toString())
            )
            )
    }
    @Test
    fun testEmptyPlaylist(){
        onView(withId(R.id.newPlaylistName))
            .perform(
                ViewActions.typeText(testPlaylist1)
            )
        closeSoftKeyboard()
        onView(withId(R.id.confirmBtn))
            .perform(click())
        onView(withId(R.id.addPlaylistErrorTextView))
            .check(
                matches(
                withText(ctx.resources.getText(R.string.playlist_is_empty).toString())
            )
            )

    }
    @Test
    fun testPlaylistWithNoName(){
        onView(withId(R.id.addSongToPlaylistArtistName))
            .perform(
                ViewActions.typeText(testSong1)
            )
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistBtn))
            .perform(click())
        onView(withId(R.id.confirmBtn))
            .perform(click())
        onView(withId(R.id.addPlaylistErrorTextView))
            .check(
                matches(
                withText(ctx.resources.getText(R.string.playlist_has_no_name).toString())
            )
            )
    }
    @Test
    fun testAddPlaylist(){

        initializeTestPlaylist()

        onView(withId(R.id.confirmBtn))
            .perform(click())

        //check that a popup has spawned and click on saveOffline
        onView(withText(R.string.saveOnlineQuestionText)) // Look for the dialog => use its title
            .inRoot(isDialog()) // check that it's indeed in a dialog
            .check(matches(isDisplayed()))

        onView(withId(R.id.saveOnlinePlaylistNo))
            .perform(click())
        Intents.intended(
            allOf(
                hasComponent(UserProfileActivity::class.java.name)
            )
        )

        val user = Tasks.await(User.loadOrDefault(ctx))
        assertTrue(user.getPlaylists().any { p -> p.name == testPlaylist1 })
        user.getPlaylists().filter { p -> p.name == testPlaylist1 }.forEach{ p ->
            run {
                assertTrue(p.getSongs().any { s -> s.name == testSong1 && s.artist == testArtist })
                assertTrue(p.getSongs().any { s -> s.name == testSong2 && s.artist == testArtist })
                assertTrue(p.getSongs().any { s -> s.name == testSong3 && s.artist == testArtist })
            }
        }
    }

    @Test
    fun clickingDoesNothing(){

        onView(withId(R.id.addSongToPlaylistSongName))
            .perform(ViewActions.typeText(testSong1))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistArtistName))
            .perform(ViewActions.typeText(testArtist))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistBtn))
            .perform(click())

        onView(withId(R.id.list_playlist_creation))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(0, click()))

        onView(withId(R.id.list_playlist_creation))
            .check(matches(hasChildCount(1)))
    }

    @Test
    fun swipingToDeleteWorks(){
        onView(withId(R.id.addSongToPlaylistSongName))
            .perform(ViewActions.typeText(testSong1))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistArtistName))
            .perform(ViewActions.typeText(testArtist))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistBtn))
            .perform(click())

        onView(withId(R.id.list_playlist_creation))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(0,
                GeneralSwipeAction(Swipe.FAST,
                    GeneralLocation.CENTER_RIGHT, GeneralLocation.CENTER_LEFT, Press.FINGER)))
        onView(withId(R.id.list_playlist_creation))
            .check(matches(hasChildCount(0)))
    }

    @Test
    fun swipingToDeleteUndoWorks(){
        onView(withId(R.id.addSongToPlaylistSongName))
            .perform(ViewActions.typeText(testSong1))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistArtistName))
            .perform(ViewActions.typeText(testArtist))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistBtn))
            .perform(click())

        onView(withId(R.id.list_playlist_creation))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(0,
            GeneralSwipeAction(Swipe.FAST,
                GeneralLocation.CENTER_RIGHT, GeneralLocation.CENTER_LEFT, Press.FINGER)))
        onView(withId(R.id.list_playlist_creation))
            .check(matches(hasChildCount(0)))
        onView(withId(R.id.snackbar_text)).check(matches(withText(R.string.deleteUndoQuestion)))
        onView(withId(R.id.snackbar_action)).perform(click())
        onView(withId(R.id.list_playlist_creation))
            .check(matches(hasChildCount(1)))
    }

    @Test
    fun pressingBackWhenNothingHasBeenModifiedGoesToUserProfile(){
        pressBack()
        Intents.intended(
            allOf(
                hasComponent(UserProfileActivity::class.java.name)
            )
        )
    }

    @Test
    fun modifyingPlaylistNameMakesPopupAppear(){
        onView(withId(R.id.newPlaylistName))
            .perform(
                ViewActions.typeText(testPlaylist1)
            )
        closeSoftKeyboard()
        pressBack()

        onView(withId(R.id.warningQuitAddPlaylist))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        onView(withId(R.id.validateQuitAddPlaylist))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))


        onView(withId(R.id.cancelQuitAddPlaylist))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun modifyingGenreMakesPopupAppear(){
        onView(withId(R.id.genreSpinner))
            .perform(
                click()
            )
        onData(allOf(`is`(instanceOf(Genre::class.java)),
            `is`(Genre.COUNTRY))).perform(click())

        pressBack()

        onView(withId(R.id.warningQuitAddPlaylist))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        onView(withId(R.id.validateQuitAddPlaylist))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))


        onView(withId(R.id.cancelQuitAddPlaylist))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun writingSongNameMakesPopupAppear(){
        onView(withId(R.id.addSongToPlaylistSongName))
            .perform(
                ViewActions.typeText(testSong1)
            )
        closeSoftKeyboard()
        pressBack()

        onView(withId(R.id.warningQuitAddPlaylist))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        onView(withId(R.id.validateQuitAddPlaylist))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))


        onView(withId(R.id.cancelQuitAddPlaylist))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun writingArtistNameMakesPopupAppear(){
        onView(withId(R.id.addSongToPlaylistArtistName))
            .perform(
                ViewActions.typeText(testArtist)
            )
        closeSoftKeyboard()
        pressBack()

        onView(withId(R.id.warningQuitAddPlaylist))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        onView(withId(R.id.validateQuitAddPlaylist))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))


        onView(withId(R.id.cancelQuitAddPlaylist))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    @Test
    fun addingASongToPlaylistMakesPopupAppear(){
        onView(withId(R.id.addSongToPlaylistSongName))
            .perform(ViewActions.typeText(testSong1))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistArtistName))
            .perform(ViewActions.typeText(testArtist))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistBtn))
            .perform(click())

        pressBack()

        onView(withId(R.id.warningQuitAddPlaylist))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        onView(withId(R.id.validateQuitAddPlaylist))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))


        onView(withId(R.id.cancelQuitAddPlaylist))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

    }

    @Test
    fun pressingOnQuitGoesToMain() {
        onView(withId(R.id.addSongToPlaylistSongName))
            .perform(ViewActions.typeText(testSong1))
        closeSoftKeyboard()
        pressBack()

        onView(withId(R.id.warningQuitAddPlaylist))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        onView(withId(R.id.validateQuitAddPlaylist))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))


        onView(withId(R.id.cancelQuitAddPlaylist))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        onView(withId(R.id.validateQuitAddPlaylist))
            .inRoot(isDialog())
            .perform(click())

        Intents.intended(
            allOf(
                hasComponent(UserProfileActivity::class.java.name)
            )
        )
    }

    @Test
    fun pressingOnCancelStaysOnCurrentActivity() {
        onView(withId(R.id.addSongToPlaylistSongName))
            .perform(ViewActions.typeText(testSong1))
        closeSoftKeyboard()
        pressBack()
        onView(withId(R.id.warningQuitAddPlaylist))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        onView(withId(R.id.validateQuitAddPlaylist))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))


        onView(withId(R.id.cancelQuitAddPlaylist))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        onView(withId(R.id.cancelQuitAddPlaylist))
            .inRoot(isDialog())
            .perform(click())

        onView(withId(R.id.addSongToPlaylistSongName))
            .check(
                matches(
                    withText(testSong1)
                )
            )
    }

    private fun initializeTestPlaylist() {
        onView(withId(R.id.newPlaylistName))
            .perform(ViewActions.typeText(testPlaylist1))
        closeSoftKeyboard()

        onView(withId(R.id.addSongToPlaylistSongName))
            .perform(ViewActions.typeText(testSong1))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistArtistName))
            .perform(ViewActions.typeText(testArtist))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistBtn))
            .perform(click())

        onView(withId(R.id.addSongToPlaylistSongName))
            .perform(ViewActions.typeText(testSong2))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistArtistName))
            .perform(ViewActions.typeText(testArtist))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistBtn))
            .perform(click())

        onView(withId(R.id.addSongToPlaylistSongName))
            .perform(ViewActions.typeText(testSong3))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistArtistName))
            .perform(ViewActions.typeText(testArtist))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistBtn))
            .perform(click())
    }
}
