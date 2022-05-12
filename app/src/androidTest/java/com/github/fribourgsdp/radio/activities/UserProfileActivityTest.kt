package com.github.fribourgsdp.radio.activities


import android.content.Context
import android.content.Intent
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.fribourgsdp.radio.MainActivity
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.data.Genre
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.data.view.MY_CLIENT_ID
import com.github.fribourgsdp.radio.data.view.PlaylistSongsFragment
import com.github.fribourgsdp.radio.data.view.REDIRECT_URI
import com.github.fribourgsdp.radio.data.view.UserProfileActivity
import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.external.google.GoogleSignInActivity
import com.github.fribourgsdp.radio.mockimplementations.GoogleUserMockUserProfileActivity
import com.github.fribourgsdp.radio.mockimplementations.MockUserProfileActivity
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import junit.framework.TestCase
import org.hamcrest.Matchers
import org.hamcrest.Matchers.allOf
import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.matches


import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class UserProfileActivityTest : TestCase() {

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
    fun changingNameAndSavingChangesChangesUser(){
        val testName = "test"

        val intent = Intent(ctx, MockUserProfileActivity::class.java)

        ActivityScenario.launch<MockUserProfileActivity>(intent).use { scenario ->
        onView(withId(R.id.saveUserButton)).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
        onView(withId(R.id.username)).perform(
            ViewActions.clearText(),
            ViewActions.typeText(testName),
            )
        onView(withId(R.id.saveUserButton)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
        Espresso.closeSoftKeyboard()
        onView(withId(R.id.saveUserButton)).perform(click())
        onView(withId(R.id.saveUserButton)).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.INVISIBLE)))
        }

        val user = User.load(ctx)
        assertEquals(testName, user.name)

    }

    @Test
    fun changingNameAndNotSavingDoesntChangeUser(){
        val testName = "testNotSave"

        val intent = Intent(ctx, MockUserProfileActivity::class.java)
        ActivityScenario.launch<MockUserProfileActivity>(intent).use { scenario ->
            onView(withId(R.id.username)).perform(
                ViewActions.clearText(),
                ViewActions.typeText(testName),
                )
            Espresso.closeSoftKeyboard()
        }
        val user = User.load(ctx)
        assertNotEquals(testName,user.name)

    }


    @Test
    fun clickOnGoogleButtonSendToGoogleSignInActivityTestOrLogoutCorrectly() {

        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, MockUserProfileActivity::class.java)
        ActivityScenario.launch<UserProfileActivity>(intent).use { scenario ->

            val googleSignInButton = onView(withId(R.id.googleSignInButton))
            googleSignInButton.perform(click())

            Intents.intended(
                Matchers.allOf(
                    IntentMatchers.hasComponent(GoogleSignInActivity::class.java.name),
                    IntentMatchers.toPackage("com.github.fribourgsdp.radio")
                )
            )
        }
    }


    @Test
    fun testBuildReqest() {
        val request = UserProfileActivity.buildRequest()
        assertEquals(MY_CLIENT_ID, request.clientId)
        assertEquals(REDIRECT_URI, request.redirectUri)
        assertTrue(request.scopes[0].equals("playlist-read-private,playlist-read-collaborative"))
    }
    @Test
    fun testPressBack(){
        val intent = Intent(ctx, UserProfileActivity::class.java)
        ActivityScenario.launch<UserProfileActivity>(intent).use { scenario ->
            Espresso.pressBack()
            Intents.intended(
                allOf(
                    hasComponent(MainActivity::class.java.name)
                )
            )
        }
    }

    @Test
    fun mergePlaylistMergesPlaylist(){
        val intent = Intent(ctx, MockUserProfileActivity::class.java)
        intent.putExtra("FromGoogle",true)
        ActivityScenario.launch<UserProfileActivity>(intent).use {

            onView(ViewMatchers.withText(R.string.MergeImportDismissPlaylistText)) // Look for the dialog => use its title
                .inRoot(RootMatchers.isDialog()) // check that it's indeed in a dialog
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            onView(withId(R.id.mergePlaylistButton))
                .perform(ViewActions.click())

        }
        var user = User.load(ctx)
        assertEquals("onlineUserTest",user.name)
        assertEquals("onlineUserTestId",user.id)
        assertEquals(2,user.getPlaylists().size)
    }

    @Test
    fun dismissPlaylistDismissedPlaylist(){

            val intent = Intent(ctx, MockUserProfileActivity::class.java)
            intent.putExtra("FromGoogle",true)
            ActivityScenario.launch<UserProfileActivity>(intent).use {

                onView(ViewMatchers.withText(R.string.MergeImportDismissPlaylistText)) // Look for the dialog => use its title
                    .inRoot(RootMatchers.isDialog()) // check that it's indeed in a dialog
                    .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

                onView(withId(R.id.dismissOnlineButton))
                    .perform(ViewActions.click())

            }
        var user = User.load(ctx)
        assertEquals("onlineUserTest",user.name)
        assertEquals("onlineUserTestId",user.id)
        assertEquals(1,user.getPlaylists().size)
        assertEquals("testTitle",user.getPlaylists().toList()[0].name)



    }

    @Test
    fun importPlaylistImportsPlaylist(){
        val intent = Intent(ctx, MockUserProfileActivity::class.java)
        intent.putExtra("FromGoogle",true)
        ActivityScenario.launch<UserProfileActivity>(intent).use {

            onView(ViewMatchers.withText(R.string.MergeImportDismissPlaylistText)) // Look for the dialog => use its title
                .inRoot(RootMatchers.isDialog()) // check that it's indeed in a dialog
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            onView(withId(R.id.importPlaylistButton))
                .perform(ViewActions.click())

        }
        val user = User.load(ctx)
        assertEquals("onlineUserTest",user.name)
        assertEquals("onlineUserTestId",user.id)
        assertEquals(1,user.getPlaylists().size)
        assertEquals("TEST_PLAYLIST",user.getPlaylists().toList()[0].id)

    }

    @Test
    fun deletePlaylistDeletesPlaylist(){
        val intent = Intent(ctx, GoogleUserMockUserProfileActivity::class.java)
        val db = Mockito.mock(Database::class.java)
        `when`(db.generateUserId()).thenReturn(Tasks.forResult(1))
        User.database = db
        ActivityScenario.launch<GoogleUserMockUserProfileActivity>(intent).use {
             onView(withId(R.id.googleSignInButton)).
                    perform(click())
            onView(ViewMatchers.withText(R.string.KeepPlaylistLocallyText)) // Look for the dialog => use its title
                .inRoot(RootMatchers.isDialog()) // check that it's indeed in a dialog
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            onView(withId(R.id.dismissPlaylistButton))
                .perform(ViewActions.click())
        }
        val user = User.load(ctx)
        assertFalse(user.isGoogleUser)
        assertEquals("Guest",user.name)
        assertEquals("1",user.id)
        assertEquals(0,user.getPlaylists().size)
    }

    @Test
    fun keepPlaylistKeepsPlaylist(){
        val intent = Intent(ctx, GoogleUserMockUserProfileActivity::class.java)

        val db = Mockito.mock(Database::class.java)
        `when`(db.generateUserId()).thenReturn(Tasks.forResult(1))
        User.database = db

        ActivityScenario.launch<GoogleUserMockUserProfileActivity>(intent).use {
            onView(withId(R.id.googleSignInButton)).
            perform(click())
            onView(ViewMatchers.withText(R.string.KeepPlaylistLocallyText)) // Look for the dialog => use its title
                .inRoot(RootMatchers.isDialog()) // check that it's indeed in a dialog
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));

            onView(withId(R.id.keepPlaylistButton))
                .perform(ViewActions.click())

        }
        val user = User.load(ctx)
        assertFalse(user.isGoogleUser)
        assertEquals("Guest",user.name)
        assertEquals("1",user.id)
        assertEquals(1,user.getPlaylists().size)
        assertEquals("testTitle",user.getPlaylists().toList()[0].name)

    }










}