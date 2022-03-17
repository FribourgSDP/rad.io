package com.github.fribourgsdp.radio

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.init
import androidx.test.espresso.intent.Intents.release
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import junit.framework.TestCase
import org.junit.Rule
import android.content.*
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import com.google.firebase.auth.*
import org.hamcrest.Matchers
import androidx.test.espresso.action.ViewActions.click

@RunWith(AndroidJUnit4::class)
class UserProfileActivityTest : TestCase() {
    @get:Rule
    val testRule = ActivityScenarioRule(
        UserProfileActivity::class.java
    )


    @Test
    fun testBuildReqest() {
        val request = UserProfileActivity.buildReqest()
        assertEquals(MY_CLIENT_ID, request.clientId)
        assertEquals(REDIRECT_URI, request.redirectUri)
        assert(request.scopes[0].equals("playlist-read-private,playlist-read-collaborative"))


    @Test
    fun logoutButtonTest() {

        Intents.init()
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithEmailAndPassword("test@test.com", "test123!!!")
        Thread.sleep(1000)

        val context: Context = ApplicationProvider.getApplicationContext()

        val intent: Intent = Intent(context, UserProfileActivity::class.java).apply {
            putExtra(USERNAME, "Default")
        }


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

        val intent: Intent = Intent(context, UserProfileActivity::class.java).apply {
            putExtra(USERNAME, "Default")
        }
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
}