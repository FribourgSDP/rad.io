package com.github.fribourgsdp.radio.ui
import androidx.fragment.app.testing.launchFragment
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.game.view.LyricsPopup
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class LyricsPopupTest {
    private val testLyrics = "Lorem Ipsum"

    @Test
    fun testCorrectTextIsDisplayed(){
        with(launchFragment(
            themeResId = R.style.Theme_Radio,
            instantiate = { LyricsPopup(testLyrics) })) {
            onView(withId(R.id.lyricsPopupTextView))
                .inRoot(isDialog())
                .check(matches(withText(testLyrics)))
        }
    }
    @Test
    fun testPopupClosingOnButtonPress(){
        with(launchFragment(
            themeResId = R.style.Theme_Radio,
            instantiate = { LyricsPopup(testLyrics) })) {
            onView(withId(R.id.close_popup_button))
                .inRoot(isDialog())
                .perform(ViewActions.click())

            onView(withId(R.id.close_popup_button))
                .check(doesNotExist())
        }
    }

}