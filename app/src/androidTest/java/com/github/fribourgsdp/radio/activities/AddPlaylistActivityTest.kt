package com.github.fribourgsdp.radio.activities

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.*
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.data.Genre
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.data.view.UserProfileActivity
import com.github.fribourgsdp.radio.mockimplementations.MockAddPlaylistActivity
import com.github.fribourgsdp.radio.util.ViewHolder
import com.google.android.gms.tasks.Tasks
import org.hamcrest.Matchers
import org.hamcrest.Matchers.*
import org.junit.After
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

        initializeSardouPlaylist()

        onView(withId(R.id.confirmBtn))
            .perform(ViewActions.click())

        //check that a popup has spawned and click on saveOffline
        onView(ViewMatchers.withText(R.string.saveOnlineQuestionText)) // Look for the dialog => use its title
            .inRoot(RootMatchers.isDialog()) // check that it's indeed in a dialog
            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

        onView(withId(R.id.saveOnlinePlaylistNo))
            .perform(ViewActions.click())
        Intents.intended(
            allOf(
                hasComponent(UserProfileActivity::class.java.name)
            )
        )

        val user = Tasks.await(User.loadOrDefault(ctx))
        assert(user.getPlaylists().any { p -> p.name == "Sardou playlist" })
        user.getPlaylists().filter { p -> p.name == "Sardou playlist" }.forEach{p ->
            run {
                assert(p.getSongs().any { s -> s.name == "Rouge" && s.artist == "Sardou" })
                assert(p.getSongs().any { s -> s.name == "En Chantant" && s.artist == "Sardou" })
                assert(p.getSongs().any { s -> s.name == "Le France" && s.artist == "Sardou" })
            }
        }
    }

    @Test
    fun clickingDoesNothing(){

        onView(withId(R.id.addSongToPlaylistSongName))
            .perform(ViewActions.typeText("Rouge"))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistArtistName))
            .perform(ViewActions.typeText("Sardou"))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistBtn))
            .perform(ViewActions.click())

        onView(withId(R.id.list_playlist_creation))
            .perform(RecyclerViewActions.actionOnItemAtPosition<ViewHolder>(0, ViewActions.click()))

        onView(withId(R.id.list_playlist_creation))
            .check(matches(hasChildCount(1)))
    }

    @Test
    fun swipingToDeleteWorks(){
        onView(withId(R.id.addSongToPlaylistSongName))
            .perform(ViewActions.typeText("Rouge"))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistArtistName))
            .perform(ViewActions.typeText("Sardou"))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistBtn))
            .perform(ViewActions.click())

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
            .perform(ViewActions.typeText("Rouge"))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistArtistName))
            .perform(ViewActions.typeText("Sardou"))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistBtn))
            .perform(ViewActions.click())

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
        Espresso.pressBack()
        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(UserProfileActivity::class.java.name)
            )
        )
    }

    @Test
    fun modifyingPlaylistNameMakesPopupAppear(){
        onView(withId(R.id.newPlaylistName))
            .perform(
                ViewActions.typeText("Sardou playlist")
            )
        closeSoftKeyboard()
        Espresso.pressBack()

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
                ViewActions.click()
            )
        onData(allOf(`is`(instanceOf(Genre::class.java)),
            `is`(Genre.COUNTRY))).perform(click())

        Espresso.pressBack()

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
                ViewActions.typeText("name")
            )
        closeSoftKeyboard()
        Espresso.pressBack()

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
                ViewActions.typeText("artistname")
            )
        closeSoftKeyboard()
        Espresso.pressBack()

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
            .perform(ViewActions.typeText("Rouge"))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistArtistName))
            .perform(ViewActions.typeText("Sardou"))
        closeSoftKeyboard()
        onView(withId(R.id.addSongToPlaylistBtn))
            .perform(ViewActions.click())

        Espresso.pressBack()

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
            .perform(ViewActions.typeText("Rouge"))
        closeSoftKeyboard()
        Espresso.pressBack()

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
            Matchers.allOf(
                IntentMatchers.hasComponent(UserProfileActivity::class.java.name)
            )
        )
    }

    @Test
    fun pressingOnCancelStaysOnCurrentActivity() {
        onView(withId(R.id.addSongToPlaylistSongName))
            .perform(ViewActions.typeText("Rouge"))
        closeSoftKeyboard()
        Espresso.pressBack()
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
            ViewAssertions.matches(
                ViewMatchers.withText("Rouge")
            ))
    }

    private fun initializeSardouPlaylist() {
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
