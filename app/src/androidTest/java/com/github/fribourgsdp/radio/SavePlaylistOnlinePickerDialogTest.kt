package com.github.fribourgsdp.radio

import androidx.fragment.app.testing.launchFragment
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class SavePlaylistOnlinePickerDialogTest {

    @Test
    fun SaveOnlineReturnTrueInListener(){
        var picked = false
        val testListener = object: SavePlaylistOnlinePickerDialog.OnPickListener{
            override fun onPick(online: Boolean){
                picked = online
            }
        }
        with(launchFragment (
            themeResId = R.style.Theme_Radio,
            instantiate = { SavePlaylistOnlinePickerDialog(testListener)}
                )){
            onView(withId(R.id.saveOnlinePlaylistYes)).perform(ViewActions.click())
            assertTrue(picked)
        }


    }

    @Test
    fun notSaveOnlineReturnFalseInListener(){
        var picked = true
        val testListener = object: SavePlaylistOnlinePickerDialog.OnPickListener{
            override fun onPick(online: Boolean){
                picked = online
            }
        }
        with(launchFragment (
            themeResId = R.style.Theme_Radio,
            instantiate = { SavePlaylistOnlinePickerDialog(testListener)}
        )){
            onView(withId(R.id.saveOnlinePlaylistNo)).perform(ViewActions.click())
            assertFalse(picked)
        }


    }



}