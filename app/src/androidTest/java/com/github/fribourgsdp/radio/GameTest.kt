package com.github.fribourgsdp.radio

import com.github.fribourgsdp.radio.backend.*
import com.github.fribourgsdp.radio.backend.gameplay.Game
import com.github.fribourgsdp.radio.backend.gameplay.User
import com.github.fribourgsdp.radio.backend.music.Genre
import com.github.fribourgsdp.radio.backend.music.Playlist
import com.github.fribourgsdp.radio.backend.music.Song
import org.junit.Test

import org.junit.Assert.*

class GameTest {
    private val playlistTest = Playlist("Test",
        mutableSetOf(
            Song("colorado", "Milky Chance"),
            Song("Back in black", "ACDC"),
            Song("i got a feeling", "black eyed pees"),
            Song("  party rock anthem", "lmfao"),
            Song("Rouge", "Sardou"),
            Song("mop", "Gunna")
        ),
        Genre.NONE
    )


    private val name = "Game Test"
    private val host = User("host")
    private val other = User("other")
    private val nbRounds = 3
    private val withHint = true
    private val isPrivate = false

    private val gameBuilder = Game.Builder().setHost(host)
        .setName(name)
        .setPlaylist(playlistTest)
        .setWithHint(withHint)
        .setNbRounds(nbRounds)
        .setPrivacy(isPrivate)
        .addUser(other)

    @Test
    fun builderWorksCorrectlyWithCorrectArgs() {
        val game = gameBuilder.build()

        assertEquals(name, game.name)
        assertEquals(host, game.host)
        assertEquals(nbRounds, game.nbRounds)
        assertEquals(withHint, game.withHint)
        assertEquals(isPrivate, game.isPrivate)
        assertEquals(false, game.isDone())
        assertEquals(playlistTest, game.playlist)
    }

    @Test
    fun choiceGetterGivesDifferentSongs() {
        val game = gameBuilder.build()

        val multipleChoices = game.getChoices(3)
        val simpleChoice1 = game.getChoices(1)
        val simpleChoice2 = game.getChoices(1)

        assertFalse(multipleChoices.containsAll(simpleChoice1))
        assertFalse(multipleChoices.containsAll(simpleChoice2))
        assertFalse(simpleChoice1.containsAll(simpleChoice2))
    }

    @Test
    fun songsResetWhenAllPicked() {
        val game = gameBuilder.build()

        val choices1 = game.getChoices(6)
        val choices2 = game.getChoices(3)

        assertTrue(choices1.containsAll(choices2))
    }

    @Test(expected = IllegalArgumentException::class)
    fun errorWhenAddingPointToFakeUser() {
        val game = gameBuilder.build()

        game.addPoints(User("fake"), 10)
    }

    @Test
    fun pointsUpdateCorrectly() {
        val game = gameBuilder.build()

        assertEquals(0, game.getScore(host))
        assertEquals(0, game.getScore(other))

        game.addPoints(host, 10)
        game.addPoints(other, 100)

        assertEquals(10, game.getScore(host))
        assertEquals(100, game.getScore(other))
    }

    @Test
    fun playerRotationWorks() {
        val game = gameBuilder.build()

        val user1 = game.getUserToPlay()
        game.getUserToPlay()
        val userTest = game.getUserToPlay()
        assertEquals(user1, userTest)
    }

    @Test
    fun currentRoundsUpdateCorrectly() {
        val game = gameBuilder.build()

        val oldValue = game.currentRound

        // The two players play in round one
        game.getUserToPlay()
        game.getUserToPlay()
        assertEquals(oldValue, game.currentRound)

        // The next chosen player plays in round two
        game.getUserToPlay()
        assertEquals(oldValue + 1, game.currentRound)
    }

    @Test
    fun gameDoneAtRightTime() {
        val game = gameBuilder.build()

        for (i in 1..nbRounds) {
            assertFalse(game.isDone())
            game.getUserToPlay()
            game.getUserToPlay()
        }

        assertTrue(game.isDone())

    }

    @Test
    fun tooManySongAsked() {
        val game = gameBuilder.build()

        assertEquals(
            playlistTest.getSongs().size,
            game.getChoices(playlistTest.getSongs().size + 15).size
        )
    }
}