package com.github.fribourgsdp.radio.mockimplementations

import com.github.fribourgsdp.radio.*

class MockGameSettingsActivity: GameSettingsActivity() {
    override fun loadUser(): User {
        val user = User("The best player")
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
        return user
    }
}