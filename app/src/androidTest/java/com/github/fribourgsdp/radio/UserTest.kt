package com.github.fribourgsdp.radio

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import junit.framework.Assert.assertEquals
import org.hamcrest.Matchers
import org.junit.Assert
import org.junit.Before

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.IllegalArgumentException


@RunWith(AndroidJUnit4::class)
class UserTest {

    @Test
    fun constructorTest(){
        val string = "test"
        val user = User(string)
        Assert.assertEquals(string, user.name)
        Assert.assertEquals(true, user.getPlaylists().isEmpty())
        Assert.assertEquals(false, user.linkedSpotify)
        Assert.assertEquals(string[0], user.initial)
    }

    @Test(expected = IllegalArgumentException::class)
    fun constructorWithNoNameThrowsIAE(){
        val string = ""
        val user = User(string)
        Assert.assertEquals(string[0], user.initial)
    }

    @Test(expected = IllegalArgumentException::class)
    fun constructorWithBlankNameThrowsIAE(){
        val string = " "
        val user = User(string)
        Assert.assertEquals(string[0], user.initial)
    }

    @Test
    fun addPlaylistWorksAsExpected(){
        val string = "test"
        val user = User(string)
        Assert.assertEquals(true, user.getPlaylists().isEmpty())
        val playlist = Playlist("test", Genre.ROCK)
        user.addPlaylist(playlist)
        Assert.assertEquals(1, user.getPlaylists().size)
        Assert.assertEquals(true, user.getPlaylists().contains(playlist))
    }

    @Test
    fun removePlaylistWorksAsExpected(){
        val string = "test"
        val user = User(string)
        val playlist = Playlist("test", Genre.ROCK)
        user.addPlaylist(playlist)
        Assert.assertEquals(1, user.getPlaylists().size)
        user.removePlaylist(playlist)
        Assert.assertEquals(true, user.getPlaylists().isEmpty())
    }

    @Test
    fun addPlaylistsWorksAsExpected(){
        val string = "test"
        val user = User(string)
        Assert.assertEquals(true, user.getPlaylists().isEmpty())
        val playlist1 = Playlist("test", Genre.ROCK)
        val playlist2 = Playlist("test2", Genre.ROCK)
        user.addPlaylists(setOf(playlist1,playlist2))
        Assert.assertEquals(2, user.getPlaylists().size)
        Assert.assertEquals(true, user.getPlaylists().contains(playlist1))
        Assert.assertEquals(true, user.getPlaylists().contains(playlist2))
    }

    @Test
    fun removePlaylistsWorksAsExpected(){
        val string = "test"
        val user = User(string)

        val playlist1 = Playlist("test", Genre.ROCK)
        val playlist2 = Playlist("test2", Genre.ROCK)
        val playlist3 = Playlist("test3", Genre.ROCK)
        user.addPlaylists(setOf(playlist1,playlist2, playlist3))
        Assert.assertEquals(3, user.getPlaylists().size)
        user.removePlaylists(setOf(playlist1, playlist2))
        Assert.assertEquals(1, user.getPlaylists().size)
        Assert.assertEquals(false, user.getPlaylists().contains(playlist1))
        Assert.assertEquals(false, user.getPlaylists().contains(playlist2))
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
        Assert.assertEquals(3, user.getPlaylists().size)
        user.save(ctx)
        val newUser = User.load(ctx)
        Assert.assertEquals(3, newUser.getPlaylists().size)
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
}