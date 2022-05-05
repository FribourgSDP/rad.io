package com.github.fribourgsdp.radio

import androidx.fragment.app.testing.launchFragment
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers
import org.junit.Assert
import org.junit.Test

class MergeDismissImportPlaylistDialogTest {

    private var picked = MergeDismissImportPlaylistDialog.Choice.DISMISS_ONLINE
    private val testListener = object: MergeDismissImportPlaylistDialog.OnPickListener{
        override fun onPick(choice: MergeDismissImportPlaylistDialog.Choice){
            picked = choice
        }
    }

    @Test
    fun mergeReturnsMergeInListener(){
        picked = MergeDismissImportPlaylistDialog.Choice.DISMISS_ONLINE
        with(launchFragment (
            themeResId = R.style.Theme_Radio,
            instantiate = { MergeDismissImportPlaylistDialog(testListener)}
        )){
            Espresso.onView(ViewMatchers.withId(R.id.mergePlaylistButton))
                .inRoot(RootMatchers.isDialog())
                .perform(ViewActions.click())
            Assert.assertEquals(MergeDismissImportPlaylistDialog.Choice.MERGE,picked)
        }
    }

    @Test
    fun importReturnsImportInListener(){
        picked = MergeDismissImportPlaylistDialog.Choice.DISMISS_ONLINE
        with(launchFragment (
            themeResId = R.style.Theme_Radio,
            instantiate = { MergeDismissImportPlaylistDialog(testListener)}
        )){
            Espresso.onView(ViewMatchers.withId(R.id.importPlaylistButton))
                .inRoot(RootMatchers.isDialog())
                .perform(ViewActions.click())
            Assert.assertEquals(MergeDismissImportPlaylistDialog.Choice.IMPORT,picked)
        }
    }

    @Test
    fun dismissOnlineReturnsDismissOnlineInListener(){
        picked = MergeDismissImportPlaylistDialog.Choice.IMPORT
        with(launchFragment (
            themeResId = R.style.Theme_Radio,
            instantiate = { MergeDismissImportPlaylistDialog(testListener)}
        )){
            Espresso.onView(ViewMatchers.withId(R.id.dismissOnlineButton))
                .inRoot(RootMatchers.isDialog())
                .perform(ViewActions.click())
            Assert.assertEquals(MergeDismissImportPlaylistDialog.Choice.DISMISS_ONLINE,picked)
        }
    }


}