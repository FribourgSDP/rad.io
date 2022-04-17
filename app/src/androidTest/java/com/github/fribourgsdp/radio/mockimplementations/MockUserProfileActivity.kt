package com.github.fribourgsdp.radio.mockimplementations

import android.content.Context
import android.os.Bundle
import com.github.fribourgsdp.radio.*
import org.mockito.Mockito
import org.mockito.Mockito.mock

class MockUserProfileActivity : UserProfileActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        User.setFSGetter(MockFileSystem.MockFSGetter)
        val string = "test"
        val user = User(string, 0)
        val playlistTitle = "testTitle"
        val playlist1 = Playlist(playlistTitle, Genre.ROCK)
        user.addPlaylists(setOf(playlist1))
        user.save(mock(Context::class.java))
        super.onCreate(savedInstanceState)
    }

    override fun checkUser() {
    }
}