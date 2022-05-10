package com.github.fribourgsdp.radio.mockimplementations

import android.content.Context
import android.os.Bundle
import com.github.fribourgsdp.radio.data.Genre
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.data.view.UserProfileActivity
import com.github.fribourgsdp.radio.database.Database
import com.google.android.gms.tasks.Tasks
import org.mockito.Mockito
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock

const val userName = "test"
const val playListName = "testTitle"
const val songName = "TestSongName"
const val userId = "testId"

class MockUserProfileActivity : UserProfileActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        User.setFSGetter(MockFileSystem.MockFSGetter)
        val user = User(userName, 0)
        user.id = userId
        user.isGoogleUser = true
        val playlist1 = Playlist(playListName, Genre.ROCK)
        val song = Song(songName, "artist")
        playlist1.addSong(song)
        user.addPlaylists(setOf(playlist1))
        user.save(mock(Context::class.java))
        super.onCreate(savedInstanceState)
    }

   /* override fun checkUser() {
    }*/
    //get and set user
    override fun initializeDatabase(): Database {
        val db = Mockito.mock(Database::class.java)
        val playlistName = "testPlaylist"
        val playlist : Playlist = Playlist(playlistName, Genre.NONE)
        playlist.id = "TEST_PLAYLIST"
        playlist.addSong(Song("rouge", "sardou"))
        playlist.addSong(Song("salut", "sardou"))
        playlist.addSong(Song("Le France", "sardou"))

        val testUser = User("onlineUserTest")
        testUser.id = "onlineUserTestId"
        testUser.addPlaylist(playlist)
        testUser.isGoogleUser = true
        Mockito.`when`(db.getUser(anyString())).thenReturn(Tasks.forResult(testUser))
        Mockito.`when`(db.setUser(anyString(),any())).thenReturn(Tasks.forResult(null))
        return db
    }

    //this is usefull in order to be able to use any() from mockito
    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }
    private fun <T> uninitialized(): T = null as T


}