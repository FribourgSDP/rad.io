package com.github.fribourgsdp.radio

import android.Manifest
import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import com.github.fribourgsdp.radio.mockimplementations.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito

/**
 * Join Game Activity Tests with working database
 * NB: Here we test on [WorkingJoinGameActivity], that uses a [LocalDatabase],
 * so that the tests don't depend on firebase.
 *
 */
@RunWith(AndroidJUnit4::class)
class WorkingJoinGameActivityTest {

    @get:Rule
    var mRuntimePermissionRule = GrantPermissionRule.grant(Manifest.permission.CAMERA)
    @get:Rule
    var mRuntimePermissionRule2 = GrantPermissionRule.grant(Manifest.permission.VIBRATE)


    @Before
    fun initIntent() {
        Intents.init()
    }

    @After
    fun releaseIntent() {
        Intents.release()
    }

    @Test
    fun intentWorksWithCorrectSettings() {
        // Test values => this one cannot exist, so it won't look in the database
        val testUID = 1001L

        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, WorkingJoinGameActivity::class.java)
        ActivityScenario.launch<WorkingJoinGameActivity>(intent).use { scenario ->

            val joinButton = Espresso.onView(ViewMatchers.withId(R.id.joinGameButton))

            joinButton.check(
                ViewAssertions.matches(
                    ViewMatchers.isNotEnabled()
                )
            )

            Espresso.onView(ViewMatchers.withId(R.id.gameToJoinID))
                .perform(ViewActions.typeText(testUID.toString()))

            Espresso.closeSoftKeyboard()

            joinButton.check(
                ViewAssertions.matches(
                    ViewMatchers.isEnabled()
                )
            )

            joinButton.perform(ViewActions.click())

            Intents.intended(
                Matchers.allOf(
                    IntentMatchers.toPackage("com.github.fribourgsdp.radio"),
                    IntentMatchers.hasComponent(LobbyActivity::class.java.name),
                    IntentMatchers.hasExtra(GAME_HOST_NAME_KEY, LocalDatabase.EXPECTED_SETTINGS.hostName),
                    IntentMatchers.hasExtra(GAME_NAME_KEY, LocalDatabase.EXPECTED_SETTINGS.name),
                    IntentMatchers.hasExtra(GAME_PLAYLIST_NAME_KEY,LocalDatabase.EXPECTED_SETTINGS.playlistName),
                    IntentMatchers.hasExtra(GAME_NB_ROUNDS_KEY, LocalDatabase.EXPECTED_SETTINGS.nbRounds),
                    IntentMatchers.hasExtra(GAME_HINT_KEY, LocalDatabase.EXPECTED_SETTINGS.withHint),
                    IntentMatchers.hasExtra(GAME_PRIVACY_KEY, LocalDatabase.EXPECTED_SETTINGS.isPrivate),
                    IntentMatchers.hasExtra(GAME_IS_HOST_KEY, false),
                    IntentMatchers.hasExtra(GAME_UID_KEY, testUID)
                )
            )
        }
    }


    @Test
    fun cancelScanQRCodeWork(){

        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, QRCodeJoinGameActivity::class.java)
        ActivityScenario.launch<QRCodeJoinGameActivity>(intent).use { scenario ->
            ViewActions.closeSoftKeyboard()
            val joinQRCodeButton = Espresso.onView(ViewMatchers.withId(R.id.joinWithQRCode))
            joinQRCodeButton.perform(ViewActions.click())

            Espresso.onView(ViewMatchers.withId(R.id.cancel_button))
                .inRoot(RootMatchers.isDialog())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

            Espresso.onView(ViewMatchers.withId(R.id.cancel_button))
                .inRoot(RootMatchers.isDialog())
                .perform(ViewActions.click())
        }
    }

    @Test
    fun joinLobbyWithScanQRCodeWork(){

        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, QRCodeJoinGameActivityJoin::class.java)
        ActivityScenario.launch<QRCodeJoinGameActivityJoin>(intent).use { scenario ->
            val displayQRCodeButton = Espresso.onView(ViewMatchers.withId(R.id.joinWithQRCode))
            displayQRCodeButton.perform(ViewActions.click())

            Espresso.onView(ViewMatchers.withId(R.id.cancel_button))
                .inRoot(RootMatchers.isDialog())
                .perform(ViewActions.click())


            Intents.intended(
                Matchers.allOf(
                    IntentMatchers.toPackage("com.github.fribourgsdp.radio"),
                    IntentMatchers.hasComponent(LobbyActivity::class.java.name),
                )
            )

        }
    }

    @Test
    fun joinFailLobbyWithScanQRCodeWork(){

        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, QRCodeJoinGameActivityJoinFail::class.java)
        ActivityScenario.launch<QRCodeJoinGameActivityJoinFail>(intent).use { scenario ->
            val displayQRCodeButton = Espresso.onView(ViewMatchers.withId(R.id.joinWithQRCode))
            displayQRCodeButton.perform(ViewActions.click())

            Espresso.onView(ViewMatchers.withId(R.id.cancel_button))
                .inRoot(RootMatchers.isDialog())
                .perform(ViewActions.click())

            Espresso.onView(ViewMatchers.withId(R.id.cancel_button))
                .inRoot(RootMatchers.isDialog())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))




        }
    }



}