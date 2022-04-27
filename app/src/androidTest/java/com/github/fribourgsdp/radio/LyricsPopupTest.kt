package com.github.fribourgsdp.radio
import androidx.fragment.app.testing.FragmentScenario
import androidx.fragment.app.testing.launchFragment
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.not
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class LyricsPopupTest {

    @Test
    fun testCorrectTextIsDisplayed(){
        with(launchFragment(
            themeResId = R.style.Theme_Radio,
            instantiate = {LyricsPopup("Lorem Ipsum")})) {
            onView(withId(R.id.lyricsPopupTextView)).check(matches(withText("Lorem Ipsum")))
        }
    }
    @Test
    fun testPopupClosingOnButtonPress(){
        with(launchFragment(
            themeResId = R.style.Theme_Radio,
            instantiate = {LyricsPopup("Lorem Ipsum")})) {
            onView(withId(R.id.close_popup_button)).perform(ViewActions.click())
            onView(withId(R.id.close_popup_button)).check(doesNotExist())
        }
    }

}