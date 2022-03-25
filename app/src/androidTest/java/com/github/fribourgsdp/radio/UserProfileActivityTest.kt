package com.github.fribourgsdp.radio

import androidx.test.espresso.Espresso
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import junit.framework.TestCase
import android.content.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.auth.*
import org.hamcrest.Matchers
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.google.android.gms.tasks.Tasks
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class UserProfileActivityTest : TestCase() {

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