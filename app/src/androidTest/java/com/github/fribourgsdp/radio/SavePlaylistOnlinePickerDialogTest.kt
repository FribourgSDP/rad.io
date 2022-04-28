package com.github.fribourgsdp.radio

import androidx.fragment.app.testing.launchFragment
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SavePlaylistOnlinePickerDialogTest {

    private var picked = false
    private val testListener = object: SavePlaylistOnlinePickerDialog.OnPickListener{
        override fun onPick(online: Boolean){
            picked = online
        }
    }

    @Test
    fun saveOnlineReturnTrueInListener(){
        picked = false
        with(launchFragment (
            themeResId = R.style.Theme_Radio,
            instantiate = { SavePlaylistOnlinePickerDialog(testListener)}
                )){
            onView(withId(R.id.saveOnlinePlaylistYes))
                .inRoot(RootMatchers.isDialog())
                .perform(ViewActions.click())
            assertTrue(picked)
        }
    }

    @Test
    fun notSaveOnlineReturnFalseInListener(){
        picked = true
        with(launchFragment (
            themeResId = R.style.Theme_Radio,
            instantiate = { SavePlaylistOnlinePickerDialog(testListener)}
        )){
            onView(withId(R.id.saveOnlinePlaylistNo))
                .inRoot(RootMatchers.isDialog())
                .perform(ViewActions.click())
            assertFalse(picked)
        }
    }



}