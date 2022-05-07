package com.github.fribourgsdp.radio.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.data.Genre
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.data.view.PLAYLIST_DATA
import com.github.fribourgsdp.radio.data.view.SONG_DATA
import com.github.fribourgsdp.radio.data.view.SongFragment
import com.github.fribourgsdp.radio.mockimplementations.MockFileSystem
import com.github.fribourgsdp.radio.mockimplementations.MockLyricsGetter
import com.github.fribourgsdp.radio.mockimplementations.MockSongFragment
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4ClassRunner::class)
class SongFragmentTest {

    @Before
    fun initIntent() {
        Intents.init()
        User.setFSGetter(MockFileSystem.MockFSGetter)
    }

    @After
    fun releaseIntent() {
        Intents.release()
    }

    @Test
    fun songAttributesAreCorrect() {
        val bundle = Bundle()
        val songName = "Testname"
        val songArtist = "TestArtist"
        val playlistName = "test"
        val playlist = Playlist(playlistName, Genre.NONE)
        val song = Song(songName, songArtist, "")
        playlist.addSong(song)
        val user : User = User("Test User")
        user.addPlaylist(playlist)
        val context: Context = ApplicationProvider.getApplicationContext()
        user.save(context)
        bundle.putString(PLAYLIST_DATA, playlist.name)
        bundle.putStringArray(SONG_DATA, arrayOf(song.name, song.artist))
        val scenario = launchFragmentInContainer<SongFragment>(bundle)
        Espresso.onView(ViewMatchers.withId(R.id.SongName))
            .check(ViewAssertions.matches(ViewMatchers.withText(songName)))
        Espresso.onView(ViewMatchers.withId(R.id.ArtistName))
            .check(ViewAssertions.matches(ViewMatchers.withText(songArtist)))
    }

    @Test
    fun songLyricsAreDisplayed() {
        val bundle = Bundle()
        val lyrics = " Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus et lobortis elit, non rutrum sem. Proin at sapien ac justo molestie scelerisque non ac enim. Cras fermentum, massa ac maximus suscipit, ex diam dapibus nulla, in mattis enim ligula ut eros. Etiam nec libero nibh. Phasellus scelerisque purus eu orci congue cursus ut tempus mi. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Aliquam luctus consequat tellus, vitae interdum felis molestie vitae. Donec dapibus tellus vel sapien laoreet tempor. Suspendisse sodales in magna eu vehicula. Mauris faucibus risus a leo vestibulum, ac vestibulum elit sodales. "
        val playlist = Playlist("test", Genre.NONE)
        val song = Song("test", "test", lyrics)
        playlist.addSong(song)
        val user : User = User("Test User")
        user.addPlaylist(playlist)
        val context: Context = ApplicationProvider.getApplicationContext()
        user.save(context)
        bundle.putString(PLAYLIST_DATA, playlist.name)
        bundle.putStringArray(SONG_DATA, arrayOf(song.name, song.artist))
        val scenario = launchFragmentInContainer<SongFragment>(bundle)
        Espresso.onView(ViewMatchers.withId(R.id.editTextLyrics))
            .check(ViewAssertions.matches(ViewMatchers.withText(lyrics)))
    }
    @Test
    fun getLyricsInSongFragment() {
        val bundle = Bundle()
        val user : User = User("Test User")
        val songName = "Momentum"
        val songArtist = "Truckfighters"
        val playlistName = "test"
        val playlist = Playlist(playlistName, Genre.NONE)
        val song = Song(songName, songArtist)
        playlist.addSong(song)
        user.addPlaylist(playlist)
        val context: Context = ApplicationProvider.getApplicationContext()
        user.save(context)
        bundle.putString(PLAYLIST_DATA, playlistName)
        bundle.putStringArray(SONG_DATA, arrayOf(song.name, song.artist))
        val scenario = launchFragmentInContainer<MockSongFragment>(bundle)
        Espresso.onView(ViewMatchers.withId(R.id.editTextLyrics))
            .check(ViewAssertions.matches(
                ViewMatchers.withText(MockLyricsGetter.truckfightersLyrics)))
    }
}