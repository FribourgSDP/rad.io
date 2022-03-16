package com.github.fribourgsdp.radio

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import junit.framework.TestCase
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test

class UserProfileActivityTest : TestCase() {
    @get:Rule
    val testRule = ActivityScenarioRule(
        UserProfileActivity::class.java
    )

    @Test
    fun testPressingOnSpotifyLoginLaunchesProcess() {
        val loginButton = Espresso.onView(withId(R.id.launchSpotifyButton))
        loginButton.perform(ViewActions.click())
        assertEquals("9dc40237547f4ffaa41bf1e07ea0bba1", MY_CLIENT_ID)
    }

    @Test
    fun testBuildReqest() {
        val request = UserProfileActivity.buildReqest()
        assertEquals(MY_CLIENT_ID, request.clientId)
        assertEquals(REDIRECT_URI, request.redirectUri)
        assert(request.scopes[0].equals("playlist-read-private,playlist-read-collaborative"))
    }
}