package com.github.fribourgsdp.radio

import org.junit.Test

import org.junit.Assert.*

class GameTest {
    private val song1 = Song("colorado", "Milky Chance")
    private val song2 = Song("Back in black", "ACDC")
    private val song3 = Song("i got a feeling", "black eyed pees")
    private val song4 = Song("  party rock anthem", "lmfao")
    private val playlistTest = Playlist("Test",
        mutableSetOf(song1, song2, song3, song4),
        Genre.NONE
    )

    @Test
    fun builderWorksCorrectlyWithCorrectArgs() {
        val builder = Game.Builder()
        val name = "Game Test"
        val host = User("host")
        val other = User("other")
        val nbRounds = 3
        val withHint = true
        val isPrivate = false

        builder.setHost(host)
            .setName(name)
            .setPlaylist(playlistTest)
            .setWithHint(withHint)
            .setNbRounds(nbRounds)
            .setPrivacy(isPrivate)
            .addUser(other)

        val game = builder.build()

        assertEquals(name, game.name)
        assertEquals(host, game.host)
        assertEquals(nbRounds, game.nbRounds)
        assertEquals(withHint, game.withHint)
        assertEquals(isPrivate, game.isPrivate)
        assertEquals(false, game.isDone)
        assertEquals(playlistTest, game.playlist)
    }
}