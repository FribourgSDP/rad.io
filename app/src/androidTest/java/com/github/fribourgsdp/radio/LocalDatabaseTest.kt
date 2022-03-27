package com.github.fribourgsdp.radio

import android.content.ContentValues
import android.util.Log
import com.github.fribourgsdp.radio.backend.database.Database
import com.github.fribourgsdp.radio.backend.gameplay.User
import com.github.fribourgsdp.radio.backend.music.Genre
import com.github.fribourgsdp.radio.backend.music.Playlist
import com.github.fribourgsdp.radio.backend.music.Song
import com.github.fribourgsdp.radio.backend.mockimplementations.LocalDatabase
import com.google.android.gms.tasks.Tasks
import org.junit.Test
import java.util.concurrent.TimeUnit

class LocalDatabaseTest
{
    private val userAuthUID = "testUser"

    @Test
    fun registeringUserAndFetchingItWorks(){
        val db : Database = LocalDatabase()
        val name = "TestingThings"
        val userTest = User(name)
        db.setUser(userAuthUID,userTest)
        val user = Tasks.withTimeout(db.getUser(userAuthUID),10, TimeUnit.SECONDS)
        assert(Tasks.await(user).name == name)
    }

    @Test
    fun fetchingUnregisteredUserReturnsNull(){
        val db : Database = LocalDatabase()
        val user = Tasks.withTimeout(db.getUser("rubbish"),10,TimeUnit.SECONDS)
        Log.d(ContentValues.TAG, "DocumentSnapshot added with ID put: " + Tasks.await(user))
        assert(Tasks.await(user) == null)
    }

    @Test
    fun registerSongAndFetchingItWorks(){
        val db : Database = LocalDatabase()
        val song1 = Song("Hello","Adele","")
        db.registerSong(song1)
        assert(Tasks.await(db.getSong(song1.name)) == song1)

    }

    @Test
    fun fetchingInexistantSongReturnsNull(){
        val db : Database = LocalDatabase()
        val song = db.getSong("Rubbish")
        assert(Tasks.await(song) == null)
    }

    @Test
    fun registerPlaylistAndFetchingItWorks(){
        val db : Database = LocalDatabase()
        val song1 = Song("Hello","Adele","")
        val song2 = Song("World","NAthan","")
        val playlist = Playlist("test", Genre.COUNTRY)
        playlist.addSong(song1)
        playlist.addSong(song2)
        db.registerPlaylist(playlist)
        assert(Tasks.await(db.getPlaylist(playlist.name)) == playlist)

    }

    @Test
    fun fetchingInexistantPlaylistReturnsNull(){
        val db : Database = LocalDatabase()
        val playlist = db.getPlaylist("Rubbish")
        assert(Tasks.await(playlist) == null)

    }

}