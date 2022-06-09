package com.github.fribourgsdp.radio.activities

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
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.game.prep.LobbyActivity
import com.github.fribourgsdp.radio.mockimplementations.QRCodeJoinGameActivity
import com.github.fribourgsdp.radio.mockimplementations.QRCodeJoinGameActivityJoin
import com.github.fribourgsdp.radio.mockimplementations.QRCodeJoinGameActivityJoinFail
import com.github.fribourgsdp.radio.utils.packageName
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class QRCodeJoinGameActivityTest {

    @Before
    fun initIntent() {
        Intents.init()
    }

    @After
    fun releaseIntent() {
        Intents.release()
    }

    @Test
    fun cancelScanQRCodeWork(){

        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, QRCodeJoinGameActivity::class.java)
        ActivityScenario.launch<QRCodeJoinGameActivity>(intent).use {
            ViewActions.closeSoftKeyboard()
            val joinQRCodeButton = Espresso.onView(ViewMatchers.withId(R.id.joinWithQRCode))
            joinQRCodeButton.perform(ViewActions.click())

            Espresso.onView(ViewMatchers.withId(R.id.cancel_button))
                .inRoot(RootMatchers.isDialog())
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

            Espresso.onView(ViewMatchers.withId(R.id.cancel_button))
                .inRoot(RootMatchers.isDialog())
                .perform(ViewActions.click())

            Espresso.onView(ViewMatchers.withId(R.id.cancel_button))
                .check(ViewAssertions.doesNotExist())
        }
    }

    @Test
    fun joinLobbyWithScanQRCodeWork(){

        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, QRCodeJoinGameActivityJoin::class.java)
        ActivityScenario.launch<QRCodeJoinGameActivityJoin>(intent).use {
            val displayQRCodeButton = Espresso.onView(ViewMatchers.withId(R.id.joinWithQRCode))
            displayQRCodeButton.perform(ViewActions.click())

            Espresso.onView(ViewMatchers.withId(R.id.cancel_button))
                .inRoot(RootMatchers.isDialog())
                .perform(ViewActions.click())


            Intents.intended(
                Matchers.allOf(
                    IntentMatchers.toPackage(packageName),
                    IntentMatchers.hasComponent(LobbyActivity::class.java.name),
                )
            )

        }
    }

    @Test
    fun joinFailLobbyWithScanQRCodeWork(){

        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, QRCodeJoinGameActivityJoinFail::class.java)
        ActivityScenario.launch<QRCodeJoinGameActivityJoinFail>(intent).use {
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