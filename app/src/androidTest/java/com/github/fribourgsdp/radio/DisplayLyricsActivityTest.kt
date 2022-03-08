package com.github.fribourgsdp.radio

import androidx.test.espresso.Espresso
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule

import org.junit.Rule
import org.junit.Test


class DisplayLyricsActivityTest {
    @get:Rule
    public val testRule = ActivityScenarioRule(
        DisplayLyricsActivity::class.java
    )
    @Test
    fun displaysNoLyricsTest(){
        val songEditView = Espresso.onView(ViewMatchers.withId(R.id.songEditView))
        val artistEditView = Espresso.onView(ViewMatchers.withId(R.id.artistEditView))
        val resultsTextView = Espresso.onView(ViewMatchers.withId(R.id.resultsTextView))
        val button = Espresso.onView(ViewMatchers.withId(R.id.button1))
        songEditView.perform(ViewActions.replaceText("Stream"))
        artistEditView.perform(ViewActions.replaceText("Dream Theater"))
        button.perform(ViewActions.click())
        Thread.sleep(1500)
        resultsTextView.check(ViewAssertions.matches(ViewMatchers.withText("---No lyrics were found for this song.---")))
    }
}