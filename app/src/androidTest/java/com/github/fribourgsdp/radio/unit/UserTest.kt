package com.github.fribourgsdp.radio.unit

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.github.fribourgsdp.radio.data.*
import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.utils.KotlinAny
import com.google.android.gms.tasks.Tasks
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito
import org.mockito.Mockito.*


@RunWith(AndroidJUnit4::class)
class UserTest {
    
    private val testString = "test"
    private val testString2 = "test2"
    private val testString3 = "test3"
    private val userName = "user"

    @Test
    fun constructorTest(){
        val string = testString
        val user = User(string)
        assertEquals(string, user.name)
        assertTrue(user.getPlaylists().isEmpty())
        assertEquals(string[0], user.initial)
    }

    @Test(expected = IllegalArgumentException::class)
    fun constructorWithNoNameThrowsIAE(){
        val string = ""
        val user = User(string)
        assertEquals(string[0], user.initial)
    }

    @Test(expected = IllegalArgumentException::class)
    fun constructorWithBlankNameThrowsIAE(){
        val string = " "
        val user = User(string)
        assertEquals(string[0], user.initial)
    }


    @Test
    fun addPlaylistWorksAsExpected(){
        val string = testString
        val user = User(string)
        assertTrue(user.getPlaylists().isEmpty())
        val playlist = Playlist(testString, Genre.ROCK)
        user.addPlaylist(playlist)
        assertEquals(1, user.getPlaylists().size)
        assertTrue(user.getPlaylists().contains(playlist))
    }

    @Test
    fun removePlaylistWorksAsExpected(){
        val string = testString
        val user = User(string)
        val playlist = Playlist(testString, Genre.ROCK)
        user.addPlaylist(playlist)
        assertEquals(1, user.getPlaylists().size)
        user.removePlaylist(playlist)
        assertTrue(user.getPlaylists().isEmpty())
    }

    @Test
    fun addPlaylistsWorksAsExpected(){
        val string = testString
        val user = User(string)
        assertTrue(user.getPlaylists().isEmpty())
        val playlist1 = Playlist(testString, Genre.ROCK)
        val playlist2 = Playlist(testString2, Genre.ROCK)
        user.addPlaylists(setOf(playlist1,playlist2))
        assertEquals(2, user.getPlaylists().size)
        assertTrue(user.getPlaylists().contains(playlist1))
        assertTrue(user.getPlaylists().contains(playlist2))
    }

    @Test
    fun removePlaylistsWorksAsExpected(){
        val string = testString
        val user = User(string)

        val playlist1 = Playlist(testString, Genre.ROCK)
        val playlist2 = Playlist(testString2, Genre.ROCK)
        val playlist3 = Playlist(testString3, Genre.ROCK)
        user.addPlaylists(setOf(playlist1,playlist2, playlist3))
        assertEquals(3, user.getPlaylists().size)
        user.removePlaylists(setOf(playlist1, playlist2))
        assertEquals(1, user.getPlaylists().size)
        assertFalse(user.getPlaylists().contains(playlist1))
        assertFalse(user.getPlaylists().contains(playlist2))
    }

    @Test
    fun addingPlaylistWithSameNameKeepsMostRecentlyAdded(){
        val playlist1 = Playlist(testString, Genre.ROCK)
        val playlist2 = Playlist(testString, Genre.POP)
        playlist2.addSong(Song(testString,""))
        assertEquals(playlist1,playlist2)
        val userTest = User(testString)
        userTest.addPlaylist(playlist1)
        userTest.addPlaylist(playlist2)
        assertEquals(1,userTest.getPlaylists().size)
        assertEquals(Genre.POP,userTest.getPlaylistWithName(testString).genre)
        assertEquals(1,userTest.getPlaylistWithName(testString).getSongs().size)
    }
    @Test
    fun savingUserToFile(){
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val string = testString
        val user = User(string, 0)

        val playlist1 = Playlist(testString, Genre.ROCK)
        val playlist2 = Playlist(testString2, Genre.ROCK)
        val playlist3 = Playlist(testString3, Genre.ROCK)
        user.addPlaylists(setOf(playlist1,playlist2, playlist3))
        assertEquals(3, user.getPlaylists().size)
        user.save(ctx)
        val newUser = User.load(ctx)
        assertEquals(3, newUser.getPlaylists().size)
    }

    @Test
    fun addSpotifyPlaylistUIdWorks(){
        val user = User(userName, 0)
        val uid = "uid"
        user.addSpotifyPlaylistUId(testString, uid)
        assertEquals(uid, user.getSpotifyPlaylistUId(testString))
    }

    @Test
    fun getSpotifyPlaylistUidWorks(){
        val user = User(userName, 0)
        val map = hashMapOf<String, String>()
        map[testString] = "bla"
        map[testString2] = "hihi"
        user.addSpotifyPlaylistUids(map)
        assertEquals("bla", user.getSpotifyPlaylistUId(testString))
    }

    @Test
    fun getSpotifyPlaylistUidWhenNonExistent(){
        assertEquals(null, User(testString, 0).getSpotifyPlaylistUId(testString2))
    }

    @Test
    fun getPlaylistWithNameWorks(){
        val user = User(userName, 0)
        val playlistName = testString2
        val playlist1 = Playlist(testString, Genre.ROCK)
        val playlist2 = Playlist(playlistName, Genre.ROCK)
        val playlist3 = Playlist(testString3, Genre.ROCK)
        user.addPlaylists(setOf(playlist1,playlist2, playlist3))
        assertEquals(playlist2, user.getPlaylistWithName(playlistName))
    }

    @Test(expected = NoSuchElementException::class)
    fun getNonExistingPlaylistWithNameThrowsException(){
        val user = User(userName, 0)
        val playlistName = testString2
        val playlist1 = Playlist(testString, Genre.ROCK)
        val playlist2 = Playlist(playlistName, Genre.ROCK)
        val playlist3 = Playlist(testString3, Genre.ROCK)
        user.addPlaylists(setOf(playlist1,playlist2, playlist3))
        user.getPlaylistWithName("doesNotExist")
    }

    @Test
    fun onlineCopyReturnsUserWithOnlyOnlinePlaylists(){
        val user = User(testString)
        val playlist1 = Playlist(testString, Genre.ROCK)
        val playlist2 = Playlist(testString2, Genre.POP)
        playlist1.savedOnline = true
        user.addPlaylist(playlist1)
        user.addPlaylist(playlist2)
        val user2 = user.onlineCopy()

        assertEquals(1,user2.getPlaylists().size)
        assertEquals(testString,user2.getPlaylists().toList()[0].name)
        assertEquals(2,user.getPlaylists().size)
        user2.name = testString3
        assertEquals(testString3,user2.name)
        assertEquals(testString,user.name)

    }


    @Test
    fun createDefaultUserWorksCorrectly(){
        val db = mock(Database::class.java)
        `when`(db.generateUserId()).thenReturn(Tasks.forResult(-3))
        User.database = db
        val user = Tasks.await(User.createDefaultUser())
        assertEquals("-3",user.id)
    }

    @Test
    fun onlineCopyAndSaveWorksCorrectly(){
        val db = mock(Database::class.java)
        `when`(db.setUser(anyString(),KotlinAny.any())).thenReturn(Tasks.forResult(null))
        User.database = db
        val user = User(testString)
        assertEquals(null,Tasks.await(user.onlineCopyAndSave()))
    }
}