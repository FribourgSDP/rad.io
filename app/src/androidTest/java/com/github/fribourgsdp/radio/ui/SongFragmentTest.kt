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
import com.github.fribourgsdp.radio.mockimplementations.MockSongFragment
import com.github.fribourgsdp.radio.utils.*
import org.junit.After
import org.junit.Before
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4ClassRunner::class)
class SongFragmentTest {
    private val user = User("Test User")

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
        val songName = testSong1
        val songArtist = testArtist
        val playlistName = testPlaylist1
        val playlist = Playlist(playlistName, Genre.NONE)
        val song = Song(songName, songArtist, "")
        playlist.addSong(song)
        user.addPlaylist(playlist)
        val context: Context = ApplicationProvider.getApplicationContext()
        user.save(context)
        bundle.putString(PLAYLIST_DATA, playlist.name)
        bundle.putStringArray(SONG_DATA, arrayOf(song.name, song.artist))
        launchFragmentInContainer<SongFragment>(bundle)
        Espresso.onView(ViewMatchers.withId(R.id.SongName))
            .check(ViewAssertions.matches(ViewMatchers.withText(songName)))
        Espresso.onView(ViewMatchers.withId(R.id.ArtistName))
            .check(ViewAssertions.matches(ViewMatchers.withText(songArtist)))
    }

    @Test
    fun songLyricsAreDisplayed() {
        val bundle = Bundle()
        val lyrics = longLyrics
        val playlist = Playlist(testPlaylist1, Genre.NONE)
        val song = Song(testSong3, testArtist, lyrics)
        playlist.addSong(song)
        user.addPlaylist(playlist)
        val context: Context = ApplicationProvider.getApplicationContext()
        user.save(context)
        bundle.putString(PLAYLIST_DATA, playlist.name)
        bundle.putStringArray(SONG_DATA, arrayOf(song.name, song.artist))
        launchFragmentInContainer<SongFragment>(bundle)
        Espresso.onView(ViewMatchers.withId(R.id.editTextLyrics))
            .check(ViewAssertions.matches(ViewMatchers.withText(lyrics)))
    }
    @Test
    fun getLyricsInSongFragment() {
        val bundle = Bundle()
        val songName = testSong6
        val songArtist = testArtist6
        val playlistName = testPlaylist2
        val playlist = Playlist(playlistName, Genre.NONE)
        val song = Song(songName, songArtist)
        playlist.addSong(song)
        user.addPlaylist(playlist)
        val context: Context = ApplicationProvider.getApplicationContext()
        user.save(context)
        bundle.putString(PLAYLIST_DATA, playlistName)
        bundle.putStringArray(SONG_DATA, arrayOf(song.name, song.artist))
        launchFragmentInContainer<MockSongFragment>(bundle)
        Espresso.onView(ViewMatchers.withId(R.id.editTextLyrics))
            .check(ViewAssertions.matches(
                ViewMatchers.withText(testLyrics6)))
    }

    @Test
    fun goodHintForSongWithoutLyrics(){
        val bundle = Bundle()
        val lyrics = ""
        val playlist = Playlist(testPlaylist1, Genre.NONE)
        val song = Song(testSong1, testArtist, lyrics)
        playlist.addSong(song)
        user.addPlaylist(playlist)
        val context: Context = ApplicationProvider.getApplicationContext()
        user.save(context)
        bundle.putString(PLAYLIST_DATA, playlist.name)
        bundle.putStringArray(SONG_DATA, arrayOf(song.name, song.artist))
        launchFragmentInContainer<SongFragment>(bundle)
        Espresso.onView(ViewMatchers.withId(R.id.editTextLyrics))
            .check(ViewAssertions.matches(ViewMatchers.withHint(R.string.add_your_lyrics)))
    }
}