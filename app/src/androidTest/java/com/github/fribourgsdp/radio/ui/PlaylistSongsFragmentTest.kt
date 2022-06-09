package com.github.fribourgsdp.radio.ui

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.utils.testArtist
import com.github.fribourgsdp.radio.utils.testSong1
import com.github.fribourgsdp.radio.utils.testSong3
import com.github.fribourgsdp.radio.utils.testSong5
import com.github.fribourgsdp.radio.data.Genre
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.data.view.AddPlaylistActivity
import com.github.fribourgsdp.radio.data.view.PLAYLIST_DATA
import com.github.fribourgsdp.radio.data.view.PlaylistSongsFragment
import com.github.fribourgsdp.radio.database.Database
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.FixMethodOrder
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4ClassRunner::class)
class PlaylistSongsFragmentTest {
    private val user = User("Test User")
    private val playlistName = "test"
    private val playlist = Playlist(playlistName, Genre.NONE)

    @Test
    fun playlistNameIsCorrect() {
        val bundle = Bundle()
        
        
        user.addPlaylist(playlist)
        val context: Context = ApplicationProvider.getApplicationContext()
        user.save(context)
        bundle.putString(PLAYLIST_DATA, playlist.name)
        launchFragmentInContainer<PlaylistSongsFragment>(bundle)
        onView(withId(R.id.PlaylistName))
            .check(matches(withText(playlistName)))
    }

    @Test
    fun playListFragmentHasRecyclerView() {
        val bundle = Bundle()
        
        
        user.addPlaylist(playlist)
        val context: Context = ApplicationProvider.getApplicationContext()
        user.save(context)
        bundle.putString(PLAYLIST_DATA, playlist.name)
        launchFragmentInContainer<PlaylistSongsFragment>(bundle)
        onView(withId(R.id.SongRecyclerView)).check(matches(isDisplayed()))
    }

    @Test
    fun editPlaylistIntent() {
        val bundle = Bundle()
        
        
        playlist.addSong(Song(testSong1, testArtist))
        playlist.addSong(Song(testSong5, testArtist))
        playlist.addSong(Song(testSong3, testArtist))
        user.addPlaylist(playlist)
        val context: Context = ApplicationProvider.getApplicationContext()
        user.save(context)
        bundle.putString(PLAYLIST_DATA, playlist.name)

        val scenario = launchFragmentInContainer<PlaylistSongsFragment>(bundle)

        scenario.use {
            Intents.init()
            onView(withId(R.id.editButton))
                .perform(ViewActions.click())
            Intents.intended(
                hasComponent(AddPlaylistActivity::class.java.name)
            )
            Intents.release()
        }
    }

    @Test
    fun deletePlaylistTest() {
        val bundle = Bundle()
        
        
        playlist.addSong(Song(testSong1, testArtist))
        playlist.addSong(Song(testSong5, testArtist))
        playlist.addSong(Song(testSong3, testArtist))
        bundle.putString(PLAYLIST_DATA, playlist.name)
        var user = this.user
        user.addPlaylist(playlist)
        val context: Context = ApplicationProvider.getApplicationContext()
        user.save(context)
        val scenario = launchFragmentInContainer<PlaylistSongsFragment>(bundle)
        scenario.use {
            onView(withId(R.id.deleteButton))
                .perform(ViewActions.click())
            user = User.load(context)
            assertTrue("${user.getPlaylists().size}", user.getPlaylists().isEmpty())
        }
    }


    @Test
    fun loadAStoredOnlinePlaylistTest() {
        val bundle = Bundle()
        
        
        playlist.id = "TEST"
        playlist.addSong(Song(testSong1, testArtist))
        playlist.addSong(Song(testSong5, testArtist))
        playlist.addSong(Song(testSong3, testArtist))

        bundle.putString(PLAYLIST_DATA, playlist.name)

        val playlistUncomplete = Playlist(playlistName, Genre.NONE)
        playlistUncomplete.savedOnline = true
        playlistUncomplete.savedLocally = false
        playlistUncomplete.id = "TEST"
        user.addPlaylist(playlistUncomplete)
        val context: Context = ApplicationProvider.getApplicationContext()
        user.save(context)
        assertEquals(0, user.getPlaylistWithName("test").getSongs().size)
        val scenario = launchFragmentInContainer<MockPlaylistSongsFragment>(bundle)
        scenario.use {
            onView(withId(R.id.SongRecyclerView)).check(matches(isDisplayed()))
            onView(withId(R.id.SongRecyclerView)).check(matches(hasChildCount(3)))
        }
        val finalUser = User.load(context)
        val pl = finalUser.getPlaylists()
        assertEquals(1, pl.size)
        assertEquals("lyrics1", pl.toList()[0].getSong(testSong1, testArtist).lyrics)
        assertEquals("lyrics2", pl.toList()[0].getSong(testSong5, testArtist).lyrics)
        assertEquals("lyrics3", pl.toList()[0].getSong(testSong3, testArtist).lyrics)

    }

    @Test
    fun clickingImportLyricsButtonImportLyrics() {
        val bundle = Bundle()
        
        
        playlist.id = "TEST"
        playlist.addSong(Song(testSong1, testArtist))
        playlist.addSong(Song(testSong5, testArtist))
        playlist.addSong(Song(testSong3, testArtist))

        bundle.putString(PLAYLIST_DATA, playlist.name)

        user.addPlaylist(playlist)
        val context: Context = ApplicationProvider.getApplicationContext()
        user.save(context)
        val scenario = launchFragmentInContainer<MockPlaylistSongsFragment>(bundle)
        scenario.use {
            onView(withId(R.id.SongRecyclerView)).check(matches(isDisplayed()))
            onView(withId(R.id.SongRecyclerView)).check(matches(hasChildCount(3)))
            onView(withId(R.id.ImportLyricsButton)).perform(ViewActions.click())
        }

        val finalUser = User.load(context)
        val pl = finalUser.getPlaylists()
        assertEquals(1, pl.size)
        assertEquals("lyrics1", pl.toList()[0].getSong(testSong1, testArtist).lyrics)
        assertEquals("lyrics2", pl.toList()[0].getSong(testSong5, testArtist).lyrics)
        assertEquals("lyrics3", pl.toList()[0].getSong(testSong3, testArtist).lyrics)


    }

    @Test
    fun buttonAreDisabledWhenOfflineAfterBeingClickedOn(){
        val bundle = Bundle()
        
        
        playlist.id = "TEST"
        playlist.savedOnline = true
        playlist.addSong(Song(testSong1, testArtist))
        playlist.addSong(Song(testSong5, testArtist))
        playlist.addSong(Song(testSong3, testArtist))

        bundle.putString(PLAYLIST_DATA, playlist.name)
        user.addPlaylist(playlist)
        val context: Context = ApplicationProvider.getApplicationContext()
        user.save(context)
        val scenario = launchFragmentInContainer<MockPlaylistSongsFragmentOffline>(bundle)
        scenario.use {

            onView(withId(R.id.ImportLyricsButton))
                .check(
                    matches(
                        isEnabled()
                    )
                )
            onView(withId(R.id.SaveOnlineButton))
                .check(
                    matches(
                        isEnabled()
                    )
                )

            onView(withId(R.id.deleteButton))
                .perform(ViewActions.click())


            onView(withId(R.id.ImportLyricsButton))
                .check(
                    matches(
                        isNotEnabled()
                    )
                )
            onView(withId(R.id.SaveOnlineButton))
                .check(
                    matches(
                        isNotEnabled()
                    )
                )
            onView(withId(R.id.deleteButton))
                .check(
                    matches(
                        isNotEnabled()
                    )
                )
            onView(withId(R.id.editButton))
                .check(
                    matches(
                        isNotEnabled()
                    )
                )
        }

    }


    class MockPlaylistSongsFragment : PlaylistSongsFragment() {
        override fun initializeDatabase(): Database {
            val playlistName = "test"
            val playlist = Playlist(playlistName, Genre.NONE)
            val db = Mockito.mock(Database::class.java)


            playlist.id = "TEST"
            playlist.addSong(Song(testSong1, testArtist, "", "1"))
            playlist.addSong(Song(testSong5, testArtist, "", "2"))
            playlist.addSong(Song(testSong3, testArtist, "", "3"))
            `when`(db.getPlaylist(anyString())).thenReturn(Tasks.forResult(playlist))
            `when`(db.getSong("1")).thenReturn(
                Tasks.forResult(
                    Song(
                        testSong1,
                        testArtist,
                        "lyrics1",
                        "1"
                    )
                )
            )
            `when`(db.getSong("2")).thenReturn(
                Tasks.forResult(
                    Song(
                        testSong5,
                        testArtist,
                        "lyrics2",
                        "2"
                    )
                )
            )
            `when`(db.getSong("3")).thenReturn(
                Tasks.forResult(
                    Song(
                        testSong3,
                        testArtist,
                        "lyrics3",
                        "3"
                    )
                )
            )


            return db
        }

        override fun loadLyrics(playlist: Playlist): Task<Void> {
            playlist.getSong(testSong1, testArtist).lyrics = "lyrics1"
            playlist.getSong(testSong5, testArtist).lyrics = "lyrics2"
            playlist.getSong(testSong3, testArtist).lyrics = "lyrics3"

            return Tasks.forResult(null)
        }
    }

    class MockPlaylistSongsFragmentOffline : PlaylistSongsFragment() {

        override fun initializeDatabase(): Database {
            return Mockito.mock(Database::class.java)
        }

        override fun loadLyrics(playlist: Playlist): Task<Void> {
            return Tasks.forException(Exception())
        }

        override fun hasConnectivity(context: Context): Boolean {
            return false
        }


    }
}