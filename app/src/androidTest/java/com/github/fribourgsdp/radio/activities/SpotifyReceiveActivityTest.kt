package com.github.fribourgsdp.radio.activities

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.data.view.UserProfileActivity
import com.github.fribourgsdp.radio.external.spotify.ImportSpotifyPlaylistsActivity
import com.github.fribourgsdp.radio.external.spotify.SpotifyReceiveActivity
import com.github.fribourgsdp.radio.utils.packageName
import com.spotify.sdk.android.auth.AccountsQueryParameters
import junit.framework.TestCase.assertEquals
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test


class SpotifyReceiveActivityTest {
    private val dummyMsg = "hehe"
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
    fun nullSpotifyResponseReturnsError(){
        val intent: Intent? = null
        assertEquals(SpotifyReceiveActivity.simpleErrorResponse, SpotifyReceiveActivity.Companion.handleSpotifyResponse(intent))
    }

    @Test
    fun emptySpotifyResponseReturnsError(){
        val intent = Intent()
        intent.data = null
        assertEquals(SpotifyReceiveActivity.simpleErrorResponse, SpotifyReceiveActivity.Companion.handleSpotifyResponse(intent))
    }

    @Test
    fun spotifyResponseTypeErrorReturnsErrorMessage(){
        val intent = Intent()
        val uri = Uri.Builder().appendQueryParameter(AccountsQueryParameters.ERROR, dummyMsg).build()
        intent.data = uri
        assertEquals(SpotifyReceiveActivity.errorResponse, SpotifyReceiveActivity.Companion.handleSpotifyResponse(intent))
    }

    @Test
    fun spotifyResponseTypeCodeReturnsUnexpectedMessage(){
        val intent = Intent()
        val uri = Uri.Builder().appendQueryParameter(AccountsQueryParameters.CODE, dummyMsg).build()
        intent.data = uri
        assertEquals(SpotifyReceiveActivity.unexpectedResponse, SpotifyReceiveActivity.Companion.handleSpotifyResponse(intent))
    }

    @Test
    fun correctSpotifyResponseReturnsToken(){
        val rawFragment = "access_token=BQCn2_RdmWU-KzsrM-VNsYU9eBHyRvR6vfIhRsiZgRMisHp76ya0U0EAXmpnhMCwOQaNO3SSAdHVQtBtSs_B_jTZT2vE29KGCnU5ZdKN4ik1cSs5rZGYG1StW3r1dGQxDoTLCjttbf1sHMa7OGYlWvvtenEDXJZ3CXiL6_-mvVnaW2ED-B8&token_type=Bearer&expires_in=3600"
        val expected = "BQCn2_RdmWU-KzsrM-VNsYU9eBHyRvR6vfIhRsiZgRMisHp76ya0U0EAXmpnhMCwOQaNO3SSAdHVQtBtSs_B_jTZT2vE29KGCnU5ZdKN4ik1cSs5rZGYG1StW3r1dGQxDoTLCjttbf1sHMa7OGYlWvvtenEDXJZ3CXiL6_-mvVnaW2ED-B8"
        val intent = Intent()
        val uri = Uri.Builder().encodedFragment(rawFragment)
            .build()
        intent.data = uri
        assertEquals(expected, SpotifyReceiveActivity.Companion.handleSpotifyResponse(intent))
    }

    @Test
    fun clickingButtonSendsToGetPlaylistActivity(){
        val intent = Intent(ctx, SpotifyReceiveActivity::class.java )

        ActivityScenario.launch<SpotifyReceiveActivity>(intent).use{ s ->
            val settingsButton = Espresso.onView(ViewMatchers.withId(R.id.importSpotifyPlaylistsButton))
            settingsButton.perform(ViewActions.click())

            Intents.intended(
                Matchers.allOf(
                    IntentMatchers.hasComponent(ImportSpotifyPlaylistsActivity::class.java.name),
                    IntentMatchers.toPackage(packageName)
                )
            )

        }
    }

    @Test
    fun clickingBackGoesBackToUserProfileActivity() {
        val intent = Intent(ctx, SpotifyReceiveActivity::class.java )
        ActivityScenario.launch<SpotifyReceiveActivity>(intent).use{
            Espresso.pressBack()

            Intents.intended(
                Matchers.allOf(
                    IntentMatchers.hasComponent(UserProfileActivity::class.java.name)
                )
            )

        }
    }
}