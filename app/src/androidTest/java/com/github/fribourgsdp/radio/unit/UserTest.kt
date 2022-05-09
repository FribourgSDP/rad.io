package com.github.fribourgsdp.radio.unit

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.github.fribourgsdp.radio.data.Genre
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.User
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class UserTest {

    @Test
    fun constructorTest(){
        val string = "test"
        val user = User(string)
        assertEquals(string, user.name)
        assertTrue(user.getPlaylists().isEmpty())
        assertFalse(user.linkedSpotify)
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
    fun defaultUserHasStarterPlaylists(){
        User.createDefaultUser().addOnSuccessListener { u ->
            val userPlaylists = u.getPlaylists()
            assertTrue(userPlaylists.contains(Playlist("Chanson française")))
            assertTrue(userPlaylists.contains(Playlist("Basic blind test")))
            assertTrue(userPlaylists.contains(Playlist("Movie theme songs")))
            assertTrue(userPlaylists.contains(Playlist("Video game songs")))
            assertTrue(userPlaylists.contains(Playlist("Classical music hits")))
        }

    }

    @Test
    fun addPlaylistWorksAsExpected(){
        val string = "test"
        val user = User(string)
        assertTrue(user.getPlaylists().isEmpty())
        val playlist = Playlist("test", Genre.ROCK)
        user.addPlaylist(playlist)
        assertEquals(1, user.getPlaylists().size)
        assertTrue(user.getPlaylists().contains(playlist))
    }

    @Test
    fun removePlaylistWorksAsExpected(){
        val string = "test"
        val user = User(string)
        val playlist = Playlist("test", Genre.ROCK)
        user.addPlaylist(playlist)
        assertEquals(1, user.getPlaylists().size)
        user.removePlaylist(playlist)
        assertTrue(user.getPlaylists().isEmpty())
    }

    @Test
    fun addPlaylistsWorksAsExpected(){
        val string = "test"
        val user = User(string)
        assertTrue(user.getPlaylists().isEmpty())
        val playlist1 = Playlist("test", Genre.ROCK)
        val playlist2 = Playlist("test2", Genre.ROCK)
        user.addPlaylists(setOf(playlist1,playlist2))
        assertEquals(2, user.getPlaylists().size)
        assertTrue(user.getPlaylists().contains(playlist1))
        assertTrue(user.getPlaylists().contains(playlist2))
    }

    @Test
    fun removePlaylistsWorksAsExpected(){
        val string = "test"
        val user = User(string)

        val playlist1 = Playlist("test", Genre.ROCK)
        val playlist2 = Playlist("test2", Genre.ROCK)
        val playlist3 = Playlist("test3", Genre.ROCK)
        user.addPlaylists(setOf(playlist1,playlist2, playlist3))
        assertEquals(3, user.getPlaylists().size)
        user.removePlaylists(setOf(playlist1, playlist2))
        assertEquals(1, user.getPlaylists().size)
        assertFalse(user.getPlaylists().contains(playlist1))
        assertFalse(user.getPlaylists().contains(playlist2))
    }

    @Test
    fun savingUserToFile(){
        val ctx = ApplicationProvider.getApplicationContext<Context>()
        val string = "test"
        val user = User(string, 0)

        val playlist1 = Playlist("test", Genre.ROCK)
        val playlist2 = Playlist("test2", Genre.ROCK)
        val playlist3 = Playlist("test3", Genre.ROCK)
        user.addPlaylists(setOf(playlist1,playlist2, playlist3))
        assertEquals(3, user.getPlaylists().size)
        user.save(ctx)
        val newUser = User.load(ctx)
        assertEquals(3, newUser.getPlaylists().size)
    }

    @Test
    fun addSpotifyPlaylistUIdWorks(){
        val user = User("nathan", 0)
        user.addSpotifyPlaylistUId("name", "uid")
        assertEquals("uid", user.getSpotifyPlaylistUId("name"))
    }

    @Test
    fun getSpotifyPlaylistUidWorks(){
        val user = User("nathan", 0)
        val map = hashMapOf<String, String>()
        map.put("test1", "bla")
        map.put("test2", "hihi")
        user.addSpotifyPlaylistUids(map)
        assertEquals("bla", user.getSpotifyPlaylistUId("test1"))
    }

    @Test
    fun getSpotifyPlaylistUidWhenNonExistent(){
        assertEquals(null, User("testUser", 0).getSpotifyPlaylistUId("none"))
    }

    @Test
    fun getPlaylistWithNameWorks(){
        val user = User("user", 0)
        val playlistName = "test2"
        val playlist1 = Playlist("test", Genre.ROCK)
        val playlist2 = Playlist(playlistName, Genre.ROCK)
        val playlist3 = Playlist("test3", Genre.ROCK)
        user.addPlaylists(setOf(playlist1,playlist2, playlist3))
        assertEquals(playlist2, user.getPlaylistWithName(playlistName))
    }

    @Test(expected = NoSuchElementException::class)
    fun getNonExistingPlaylistWithNameThrowsException(){
        val user = User("user", 0)
        val playlistName = "test2"
        val playlist1 = Playlist("test", Genre.ROCK)
        val playlist2 = Playlist(playlistName, Genre.ROCK)
        val playlist3 = Playlist("test3", Genre.ROCK)
        user.addPlaylists(setOf(playlist1,playlist2, playlist3))
        user.getPlaylistWithName("doesNotExist")
    }
}