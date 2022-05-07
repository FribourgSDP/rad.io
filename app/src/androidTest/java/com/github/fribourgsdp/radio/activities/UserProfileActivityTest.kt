package com.github.fribourgsdp.radio.activities


import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.fribourgsdp.radio.MainActivity
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.data.view.MY_CLIENT_ID
import com.github.fribourgsdp.radio.data.view.REDIRECT_URI
import com.github.fribourgsdp.radio.data.view.UserProfileActivity
import com.github.fribourgsdp.radio.external.google.GoogleSignInActivity
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

        val intent = Intent(ctx, MainActivity::class.java)
        ActivityScenario.launch<MainActivity>(intent).use { scenario ->
            onView(withId(R.id.profileButton)).perform(click())
        }

        onView(withId(R.id.username)).perform(
            ViewActions.clearText(),
            ViewActions.typeText(testName),
            )

        Espresso.closeSoftKeyboard()
        onView(withId(R.id.saveUserButton)).perform(click())

        val user = User.load(ctx)

        assertEquals(testName, user.name)

    }

    @Test
    fun changingNameAndNotSavingDoesntChangeUser(){
        val testName = "testNotSave"

        val intent = Intent(ctx, MainActivity::class.java)
        ActivityScenario.launch<MainActivity>(intent).use { scenario ->
            onView(withId(R.id.profileButton)).perform(click())
        }

        onView(withId(R.id.username)).perform(
            ViewActions.clearText(),
            ViewActions.typeText(testName),
            )

        Espresso.closeSoftKeyboard()

        val user = User.load(ctx)
        assertNotEquals(testName,user.name)

    }

    @Test
    fun homeButtonTest() {
        val context: Context = ApplicationProvider.getApplicationContext()

        val intent = Intent(context, UserProfileActivity::class.java)
        ActivityScenario.launch<UserProfileActivity>(intent).use { scenario ->

            val googleSignInButton = Espresso.onView(ViewMatchers.withId(R.id.homeButton))
            googleSignInButton.perform(click())

            Intents.intended(
                Matchers.allOf(
                    IntentMatchers.hasComponent(MainActivity::class.java.name),
                    IntentMatchers.toPackage("com.github.fribourgsdp.radio")
                )
            )
        }

    }

    @Test
    fun clickOnGoogleButtonSendToGoogleSignInActivityTestOrLogoutCorrectly() {
        val firebaseAuth = FirebaseAuth.getInstance()
        val task = Tasks.withTimeout(firebaseAuth.signInWithEmailAndPassword("test@test.com", "test123!!!"),10, TimeUnit.SECONDS)
        Tasks.await(task)
        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, UserProfileActivity::class.java)
        ActivityScenario.launch<UserProfileActivity>(intent).use { scenario ->

            val googleSignInButton = onView(withId(R.id.googleSignInButton))
            googleSignInButton.perform(click())
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
}