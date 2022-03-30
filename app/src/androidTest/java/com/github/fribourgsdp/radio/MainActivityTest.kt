package com.github.fribourgsdp.radio

import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Main Activity Tests
 *
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule
    var mainActivityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun playButtonStartsGameSettings() {
        Intents.init()
        val playButton = Espresso.onView(withId(R.id.playButton))
        playButton.perform(ViewActions.click())

        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(GameSettingsActivity::class.java.name),
                IntentMatchers.toPackage("com.github.fribourgsdp.radio")
            )
        )

        Intents.release()
    }

    @Test
    fun settingsButtonStartsSettings() {
        Intents.init()
        val settingsButton = Espresso.onView(withId(R.id.settingsButton))
        settingsButton.perform(ViewActions.click())

        Intents.intended(
            Matchers.allOf(
                IntentMatchers.hasComponent(SettingsActivity::class.java.name),
                IntentMatchers.toPackage("com.github.fribourgsdp.radio")
            )
        )

        Intents.release()
    }

    @Test
    fun correctTransitionToDisplayLyricsActivity(){
        Intents.init()
        Espresso.onView(withId(R.id.button)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(DisplayLyricsActivity::class.java.name))
        Intents.release()
    }

    @Test
    fun correctTransitionToUserProfile(){
        Intents.init()
        Espresso.onView(withId(R.id.profileButton)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(UserProfileActivity::class.java.name))
        Intents.release()
    }
    @Test
    fun correctTransitionToFireBaseTestActivity(){
        Intents.init()
        Espresso.onView(withId(R.id.FireBaseButton)).perform(ViewActions.click())
        Intents.intended(IntentMatchers.hasComponent(CreateUserProfileActivity::class.java.name))
        Intents.release()
    }
}