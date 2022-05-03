package com.github.fribourgsdp.radio

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
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.spotify.sdk.android.auth.AccountsQueryParameters
import junit.framework.TestCase.assertEquals
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


class SpotifyReceiveActivityTest {
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
        assertEquals("Error", SpotifyReceiveActivity.Companion.handleSpotifyResponse(intent))
    }

    @Test
    fun emptySpotifyResponseReturnsError(){
        val intent = Intent()
        intent.data = null
        assertEquals("Error", SpotifyReceiveActivity.Companion.handleSpotifyResponse(intent))
    }

    @Test
    fun spotifyResponseTypeErrorReturnsErrorMessage(){
        val intent = Intent()
        val uri = Uri.Builder().appendQueryParameter(AccountsQueryParameters.ERROR, "hehe").build()
        intent.data = uri
        assertEquals("Error occured.", SpotifyReceiveActivity.Companion.handleSpotifyResponse(intent))
    }

    @Test
    fun spotifyResponseTypeCodeReturnsUnexpectedMessage(){
        val intent = Intent()
        val uri = Uri.Builder().appendQueryParameter(AccountsQueryParameters.CODE, "hehe").build()
        intent.data = uri
        assertEquals("Something unexpected occured.", SpotifyReceiveActivity.Companion.handleSpotifyResponse(intent))
    }

    @Test
    fun correctSpotifyResponseReturnsToken(){
        val intent = Intent()
        val uri = Uri.Builder().encodedFragment("access_token=BQCn2_RdmWU-KzsrM-VNsYU9eBHyRvR6vfIhRsiZgRMisHp76ya0U0EAXmpnhMCwOQaNO3SSAdHVQtBtSs_B_jTZT2vE29KGCnU5ZdKN4ik1cSs5rZGYG1StW3r1dGQxDoTLCjttbf1sHMa7OGYlWvvtenEDXJZ3CXiL6_-mvVnaW2ED-B8&token_type=Bearer&expires_in=3600")
            .build()
        intent.data = uri
        assertEquals("BQCn2_RdmWU-KzsrM-VNsYU9eBHyRvR6vfIhRsiZgRMisHp76ya0U0EAXmpnhMCwOQaNO3SSAdHVQtBtSs_B_jTZT2vE29KGCnU5ZdKN4ik1cSs5rZGYG1StW3r1dGQxDoTLCjttbf1sHMa7OGYlWvvtenEDXJZ3CXiL6_-mvVnaW2ED-B8", SpotifyReceiveActivity.Companion.handleSpotifyResponse(intent))
    }

    @Test
    fun clickingButtonSendsToGetPlaylistActivity(){
        val intent = Intent(ctx,SpotifyReceiveActivity::class.java )

        ActivityScenario.launch<SpotifyReceiveActivity>(intent).use{ s ->
            val settingsButton = Espresso.onView(ViewMatchers.withId(R.id.importSpotifyPlaylistsButton))
            settingsButton.perform(ViewActions.click())

            Intents.intended(
                Matchers.allOf(
                    IntentMatchers.hasComponent(ImportSpotifyPlaylistsActivity::class.java.name),
                    IntentMatchers.toPackage("com.github.fribourgsdp.radio")
                )
            )

        }
    }
}