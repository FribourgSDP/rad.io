package com.github.fribourgsdp.radio.unit

import android.content.ContentValues
import android.util.Log
import com.github.fribourgsdp.radio.data.Genre
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.mockimplementations.LocalDatabase
import com.google.android.gms.tasks.Tasks
import org.junit.Test
import java.util.concurrent.TimeUnit
import org.junit.Assert.*

class LocalDatabaseTest
{
    private val userAuthUID = "testUser"

    private val song1 = Song("Hello","Adele","")
    private val song2 = Song("World","NAthan","")

    private val playlist = Playlist("test", Genre.COUNTRY)
    private val rubbish = "rubbish"

    @Test
    fun registeringUserAndFetchingItWorks(){
        val db : Database = LocalDatabase()
        val name = "TestingThings"
        val userTest = User(name)
        db.setUser(userAuthUID,userTest)
        val user = Tasks.withTimeout(db.getUser(userAuthUID),10, TimeUnit.SECONDS)
        assertEquals(name ,Tasks.await(user).name )
    }

    @Test
    fun fetchingUnregisteredUserReturnsNull(){
        val db : Database = LocalDatabase()
        val user = Tasks.withTimeout(db.getUser(rubbish),10,TimeUnit.SECONDS)
        Log.d(ContentValues.TAG, "DocumentSnapshot added with ID put: " + Tasks.await(user))
        assertEquals(null, Tasks.await(user))
    }

    @Test
    fun registerSongAndFetchingItWorks(){
        val db : Database = LocalDatabase()
        db.registerSong(song1)
        assertEquals(song1, Tasks.await(db.getSong(song1.name)))

    }

    @Test
    fun fetchingNonExistentSongReturnsNull(){
        val db : Database = LocalDatabase()
        val song = db.getSong(rubbish)
        assertEquals(null ,Tasks.await(song))
    }

    @Test
    fun registerPlaylistAndFetchingItWorks(){
        val db : Database = LocalDatabase()
        playlist.addSong(song1)
        playlist.addSong(song2)
        db.registerPlaylist(playlist)
        assertEquals(playlist,Tasks.await(db.getPlaylist(playlist.name)) )

    }

    @Test
    fun fetchingNonExistentPlaylistReturnsNull(){
        val db : Database = LocalDatabase()
        val playlist = db.getPlaylist(rubbish)
        assertEquals(null,Tasks.await(playlist))

    }

}