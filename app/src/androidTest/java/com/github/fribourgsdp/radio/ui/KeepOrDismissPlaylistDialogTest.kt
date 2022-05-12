package com.github.fribourgsdp.radio.ui

import androidx.fragment.app.testing.launchFragment
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import com.github.fribourgsdp.radio.data.view.KeepOrDismissPlaylistDialog
import com.github.fribourgsdp.radio.R
import org.junit.Assert
import org.junit.Test

class KeepOrDismissPlaylistDialogTest {

    private var picked = KeepOrDismissPlaylistDialog.Choice.KEEP
    private val testListener = object: KeepOrDismissPlaylistDialog.OnPickListener {
        override fun onPick(choice: KeepOrDismissPlaylistDialog.Choice){
            picked = choice
        }
    }

    @Test
    fun keepReturnKeepsInListener(){
        picked = KeepOrDismissPlaylistDialog.Choice.DISMISS
        with(launchFragment (
            themeResId = R.style.Theme_Radio,
            instantiate = { KeepOrDismissPlaylistDialog(testListener) }
        )){
            Espresso.onView(ViewMatchers.withId(R.id.keepPlaylistButton))
                .inRoot(RootMatchers.isDialog())
                .perform(ViewActions.click())
            Assert.assertEquals(KeepOrDismissPlaylistDialog.Choice.KEEP,picked)
        }
    }

    @Test
    fun dismissReturnsDismissInListener(){
        picked = KeepOrDismissPlaylistDialog.Choice.KEEP
        with(launchFragment (
            themeResId = R.style.Theme_Radio,
            instantiate = { KeepOrDismissPlaylistDialog(testListener) }
        )){
            Espresso.onView(ViewMatchers.withId(R.id.dismissPlaylistButton))
                .inRoot(RootMatchers.isDialog())
                .perform(ViewActions.click())
            Assert.assertEquals(KeepOrDismissPlaylistDialog.Choice.DISMISS,picked)
        }
    }


}