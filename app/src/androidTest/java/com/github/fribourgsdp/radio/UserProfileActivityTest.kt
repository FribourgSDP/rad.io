package com.github.fribourgsdp.radio

import androidx.test.espresso.Espresso
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import junit.framework.TestCase
import android.content.*
import android.util.Log
import android.view.KeyEvent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import com.google.firebase.auth.*
import org.hamcrest.Matchers
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.google.android.gms.tasks.Tasks
import org.junit.Rule
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class UserProfileActivityTest : TestCase() {


   // @get:Rule
    //var userProfileActivityRule = ActivityScenarioRule(UserProfileActivity::class.java)

    private val ctx: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun userSavedLocallyIsDisplayed(){
        Intents.init()

        val testUser = User("Test")
        testUser.save(ctx)
        val intent: Intent = Intent(ctx, UserProfileActivity::class.java)

        ActivityScenario.launch<UserProfileActivity>(intent).use { scenario ->

            onView(withId(R.id.username)).check(matches(withText(testUser.name)));


        }
        Intents.release()

    }

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

        val intent: Intent = Intent(context, UserProfileActivity::class.java)

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
    fun NoUserConnectedTest() {

        Intents.init()
        val context: Context = ApplicationProvider.getApplicationContext()

        val intent: Intent = Intent(context, UserProfileActivity::class.java)
        ActivityScenario.launch<UserProfileActivity>(intent).use { scenario ->

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