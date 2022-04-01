package com.github.fribourgsdp.radio

import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4ClassRunner::class)
class PlaylistSongsFragmentTest {

    @Test
    fun playlistNameIsCorrect() {
        val bundle = Bundle()
        val playlistName = "test"
        val playlist : Playlist = Playlist(playlistName, Genre.NONE)
        bundle.putString(PLAYLIST_DATA, Json.encodeToString(playlist))
        val scenario = launchFragmentInContainer<PlaylistSongsFragment>(bundle)
        Espresso.onView(withId(R.id.PlaylistName))
            .check(ViewAssertions.matches(ViewMatchers.withText(playlistName)))
    }

    @Test
    fun playListFragmentHasRecyclerView() {
        val bundle = Bundle()
        val playlistName = "test"
        val playlist : Playlist = Playlist(playlistName, Genre.NONE)
        bundle.putString(PLAYLIST_DATA, Json.encodeToString(playlist))
        val scenario = launchFragmentInContainer<PlaylistSongsFragment>(bundle)
        Espresso.onView(withId(R.id.SongRecyclerView)).check(matches(isDisplayed()))
    }
}