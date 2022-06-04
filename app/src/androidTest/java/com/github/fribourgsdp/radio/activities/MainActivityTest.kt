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
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.fribourgsdp.radio.MainActivity
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.config.SettingsActivity
import com.github.fribourgsdp.radio.data.view.UserProfileActivity
import com.github.fribourgsdp.radio.game.prep.GameSettingsActivity
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

const val packageName = "com.github.fribourgsdp.radio"

/**
 * Main Activity Tests
 *
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

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
    fun playButtonStartsGameSettings() {
        val intent = Intent(ctx, MainActivity::class.java)

        ActivityScenario.launch<MainActivity>(intent).use {
            val playButton = Espresso.onView(withId(R.id.playButton))
            playButton.perform(ViewActions.click())

            Intents.intended(
                Matchers.allOf(
                    IntentMatchers.hasComponent(GameSettingsActivity::class.java.name),
                    IntentMatchers.toPackage(packageName)
                )
           )
        }

    }

    @Test
    fun settingsButtonStartsSettings() {
        val intent = Intent(ctx, MainActivity::class.java)

        ActivityScenario.launch<MainActivity>(intent).use {
            val settingsButton = Espresso.onView(withId(R.id.settingsButton))
            settingsButton.perform(ViewActions.click())

            Intents.intended(
                Matchers.allOf(
                    IntentMatchers.hasComponent(SettingsActivity::class.java.name),
                    IntentMatchers.toPackage(packageName)
                )
            )
        }
    }

    @Test
    fun correctTransitionToUserProfile(){
        val intent = Intent(ctx, MainActivity::class.java)

        ActivityScenario.launch<MainActivity>(intent).use {
            Espresso.onView(withId(R.id.profileButton)).perform(ViewActions.click())
            Intents.intended(IntentMatchers.hasComponent(UserProfileActivity::class.java.name))
        }
    }

    @Test
    fun onBackPressedStaysOnMainActivity(){
        val intent = Intent(ctx, MainActivity::class.java)

        ActivityScenario.launch<MainActivity>(intent).use {
            Espresso.pressBack()
            val newContext: Context = ApplicationProvider.getApplicationContext()
            assertEquals(ctx, newContext)
        }
    }

    @Test
    fun playButtonIsDisabledWhenOffline(){
        val intent = Intent(ctx, MockMainActivity::class.java)

        ActivityScenario.launch<MockMainActivity>(intent).use {
            Espresso.onView(withId(R.id.playButton))
                .check(
                    ViewAssertions.matches(
                        ViewMatchers.isNotEnabled()
                    )
                )
        }


        }
}

class MockMainActivity : MainActivity(){

    override fun hasConnectivity(context: Context): Boolean {
        return false
    }

}