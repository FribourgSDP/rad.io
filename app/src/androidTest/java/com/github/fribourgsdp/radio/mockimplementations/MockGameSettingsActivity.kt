package com.github.fribourgsdp.radio.mockimplementations

import android.content.Context
import android.os.Bundle
import com.github.fribourgsdp.radio.*
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.data.User
import org.mockito.Mockito.*

class MockGameSettingsActivity: GameSettingsActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        val context = mock(Context::class.java)
        val user = User("The best player")
        MockFileSystem.wipeData()
        User.setFSGetter(MockFileSystem.MockFSGetter)
        user.addPlaylists(
            setOf(
                Playlist("Rap Playlist"),
                Playlist(
                    "French Playlist",
                    setOf(
                        Song("Rouge", "Sardou"),
                        Song("La chenille", "La Bande à Basile"),
                        Song("Allumer le feu", "Johnny Hallyday"),
                        Song("Que Je T'aime", "Johnny Hallyday")
                    ),
                    Genre.NONE
                ),
                Playlist("Country Playlist")
            )
        )
        user.save(context)
        super.onCreate(savedInstanceState)
    }
}