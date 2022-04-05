package com.github.fribourgsdp.radio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4


@RunWith(AndroidJUnit4::class)
class SongPickerDialogTest {

    private val listSongs = arrayListOf("Wesh alors", "Mamma Mia!", "La Bohème")

    /**
     * Launch SongPickerDialog
     */
    private fun launchSongPickerDialog(choices: List<String>, listener: GameView.OnPickListener): FragmentScenario<SongPickerDialog> {
        return launchFragment(
            themeResId = R.style.Theme_Radio,
            instantiate = { SongPickerDialog(choices, listener) }
        )
    }

    @Test
    fun songPickerWithCorrectSongs() {
        val uselessListener = object: GameView.OnPickListener {
            override fun onPick(song: String) {
                // DO NOTHING
            }
        }

        with(launchSongPickerDialog(listSongs, uselessListener)) {
            onView(withId(R.id.songPick0))
                .check(matches(withText(listSongs[0])))
                .check(matches(isDisplayed()))

            onView(withId(R.id.songPick1))
                .check(matches(withText(listSongs[1])))
                .check(matches(isDisplayed()))

            onView(withId(R.id.songPick2))
                .check(matches(withText(listSongs[2])))
                .check(matches(isDisplayed()))

            onFragment { songPicker ->
                songPicker.dismiss()
            }
        }

    }

    @Test
    fun songPickerReturnsCorrectPick() {
        val testListener = object: GameView.OnPickListener {
            var pick = ""
            override fun onPick(song: String) {
                pick = song
            }

        }

        with(launchSongPickerDialog(listSongs, testListener)) {
            onView(withId(R.id.songPick0))
                .perform(ViewActions.click())

            assertEquals(listSongs[0], testListener.pick)
        }
    }

    @Test
    fun hideDialogButtonOnLessSongs() {
        val uselessListener = object: GameView.OnPickListener {
            override fun onPick(song: String) {
                // DO NOTHING
            }
        }

        with(launchSongPickerDialog(listSongs.subList(0, listSongs.size - 2), uselessListener)) {
            onView(withId(R.id.songPick2))
                .check(matches(not(isDisplayed())))

            onFragment { songPicker ->
                songPicker.dismiss()
            }
        }
    }
}