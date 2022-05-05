package com.github.fribourgsdp.radio.mockimplementations

import android.content.Context
import android.os.Bundle
import com.github.fribourgsdp.radio.*
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.Song
import org.mockito.Mockito.mock

const val userName = "test"
const val playListName = "testTitle"
const val songName = "TestSongName"

class MockUserProfileActivity : UserProfileActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        User.setFSGetter(MockFileSystem.MockFSGetter)
        val user = User(userName, 0)
        val playlist1 = Playlist(playListName, Genre.ROCK)
        val song = Song(songName, "artist")
        playlist1.addSong(song)
        user.addPlaylists(setOf(playlist1))
        user.save(mock(Context::class.java))
        super.onCreate(savedInstanceState)
    }

    override fun checkUser() {
    }
}