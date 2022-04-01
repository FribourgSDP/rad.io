package com.github.fribourgsdp.radio

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import junit.framework.TestCase
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions

import androidx.test.espresso.matcher.ViewMatchers.*


import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class UserProfileActivityTest : TestCase() {
    @get:Rule
    var userProfileActivityRule = ActivityScenarioRule(UserProfileActivity::class.java)


   // @get:Rule
    //var userProfileActivityRule = ActivityScenarioRule(UserProfileActivity::class.java)

    private val ctx: Context = ApplicationProvider.getApplicationContext()

   
    @Test
    fun changingNameAndSavingChangesChangesUser(){
        val testName = "test"

        val intent: Intent = Intent(ctx, MainActivity::class.java)
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

        assert(user.name == testName)

    }

    @Test
    fun changingNameAndNotSavingDoesntChangeUser(){
        val testName = "testNotSave"

        val intent: Intent = Intent(ctx, MainActivity::class.java)
        ActivityScenario.launch<MainActivity>(intent).use { scenario ->


            onView(withId(R.id.profileButton)).perform(click())
        }

        onView(withId(R.id.username)).perform(
            ViewActions.clearText(),
            ViewActions.typeText(testName),

            )

        Espresso.closeSoftKeyboard()
        //onView(withId(R.id.saveUserButton)).perform(click())

        val user = User.load(ctx)

        assert(user.name != testName)

    }

    @Test
    fun logoutButtonTest() {

        Intents.init()
        val firebaseAuth = FirebaseAuth.getInstance()
        val task = Tasks.withTimeout(firebaseAuth.signInWithEmailAndPassword("test@test.com", "test123!!!"),10, TimeUnit.SECONDS)
        Tasks.await(task)
        val context: Context = ApplicationProvider.getApplicationContext()

        val intent = Intent(context, UserProfileActivity::class.java)

        ActivityScenario.launch<UserProfileActivity>(intent).use { scenario ->

            val logoutButton = Espresso.onView(ViewMatchers.withId(R.id.logoutButton))
            logoutButton.perform(click())

            Intents.intended(
                Matchers.allOf(
                    IntentMatchers.hasComponent(MainActivity::class.java.name),
                    IntentMatchers.toPackage("com.github.fribourgsdp.radio")
                )
            )
        }
        Intents.release()
    }

    @Test
    fun pressingBackButtonTakesUserToMainActivity(){
        Intents.init()
        Espresso.pressBack()
        Intents.intended(
            IntentMatchers.hasComponent(MainActivity::class.java.name)
        )
        Intents.release()
    }


    @Test
    fun cliclOnGoogleButtonSendToGoogleSignInActivityTest() {

        Intents.init()
        val context: Context = ApplicationProvider.getApplicationContext()

        val intent = Intent(context, UserProfileActivity::class.java)
        ActivityScenario.launch<UserProfileActivity>(intent).use { scenario ->

            val googleSignInButton = Espresso.onView(ViewMatchers.withId(R.id.googleSignInButton))
            googleSignInButton.perform(click())

            Intents.intended(
                Matchers.allOf(
                    IntentMatchers.hasComponent(GoogleSignInActivity::class.java.name),
                    IntentMatchers.toPackage("com.github.fribourgsdp.radio")
                )
            )
        }
        Intents.release()
    }


    @Test
    fun testBuildReqest() {
        val request = UserProfileActivity.buildRequest()
        assertEquals(MY_CLIENT_ID, request.clientId)
        assertEquals(REDIRECT_URI, request.redirectUri)
        assert(request.scopes[0].equals("playlist-read-private,playlist-read-collaborative"))
    }
}