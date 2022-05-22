package com.github.fribourgsdp.radio.mockimplementations

import android.content.Context
import android.os.Bundle
import com.github.fribourgsdp.radio.data.Genre
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.data.view.UserProfileActivity
import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.utils.KotlinAny
import com.google.android.gms.tasks.Tasks
import org.mockito.Mockito
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock

const val userName = "test"
const val playListName = "testTitle"
const val songName = "TestSongName"
const val userId = "testId"
const val artistName = "artist"
const val playlistName = "testPlaylist"
const val testPlaylistId = "TEST_PLAYLIST"
const val onlineUserId = "onlineUserTestId"
const val onlineUserName = "onlineUserTest"

class GoogleUserMockUserProfileActivity : UserProfileActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        User.setFSGetter(MockFileSystem.MockFSGetter)
        val user = User(userName, 0)
        user.isGoogleUser = true
        user.id = userId
        val playlist1 = Playlist(playListName, Genre.ROCK)
        val song = Song(songName, artistName)
        playlist1.addSong(song)
        user.addPlaylists(setOf(playlist1))
        user.save(mock(Context::class.java))
        super.onCreate(savedInstanceState)
    }


    override fun initializeDatabase(): Database {
        val db = Mockito.mock(Database::class.java)

        val playlist : Playlist = Playlist(playlistName, Genre.NONE)
        playlist.id = testPlaylistId
        playlist.addSong(Song("rouge", "sardou"))
        playlist.addSong(Song("salut", "sardou"))
        playlist.addSong(Song("Le France", "sardou"))

        val testUser = User(onlineUserName)
        testUser.id = onlineUserId
        testUser.addPlaylist(playlist)
        testUser.isGoogleUser = true
        Mockito.`when`(db.getUser(anyString())).thenReturn(Tasks.forResult(testUser))
        Mockito.`when`(db.setUser(anyString(), KotlinAny.any())).thenReturn(Tasks.forResult(null))
        return db
    }




}

open class MockUserProfileActivity : UserProfileActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        User.setFSGetter(MockFileSystem.MockFSGetter)
        val user = User(userName, 0)
        user.id = userId
        val playlist1 = Playlist(playListName, Genre.ROCK)
        val song = Song(songName, artistName)
        playlist1.addSong(song)
        user.addPlaylists(setOf(playlist1))
        user.save(mock(Context::class.java))
        super.onCreate(savedInstanceState)
    }

    override fun initializeDatabase(): Database {
        val db = mock(Database::class.java)
        val playlist = Playlist(playlistName, Genre.NONE)
        playlist.id = testPlaylistId
        playlist.addSong(Song("rouge", "sardou"))
        playlist.addSong(Song("salut", "sardou"))
        playlist.addSong(Song("Le France", "sardou"))

        val testUser = User(onlineUserName)
        testUser.id = onlineUserId
        testUser.addPlaylist(playlist)
        testUser.isGoogleUser = true
        Mockito.`when`(db.getUser(anyString())).thenReturn(Tasks.forResult(testUser))
        Mockito.`when`(db.setUser(anyString(),KotlinAny.any())).thenReturn(Tasks.forResult(null))
        return db
    }


}