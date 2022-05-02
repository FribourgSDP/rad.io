package com.github.fribourgsdp.radio

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
    private val otherId = "other_id"
    private val nbRounds = 3
    private val withHint = true
    private val isPrivate = false

    private val gameBuilder = Game.Builder().setHost(host)
        .setName(name)
        .setPlaylist(playlistTest)
        .setWithHint(withHint)
        .setNbRounds(nbRounds)
        .setPrivacy(isPrivate)
        .addUserId(otherId)
/**
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
        game.addPoints("fake", 10)
    }

    @Test
    fun pointsUpdateCorrectly() {
        val game = gameBuilder.build()

        assertEquals(0L, game.getScore(host.id))
        assertEquals(0L, game.getScore(otherId))

        game.addPoints(host.id, 10L)
        game.addPoints(otherId, 100L)

        assertEquals(10L, game.getScore(host.id))
        assertEquals(100L, game.getScore(otherId))
    }

    @Test
    fun pointsUpdateCorrectlyWithMapFunctions() {
        val game = gameBuilder.build()

        assertTrue(game.getAllScores().all { (_, value) -> value == 0L })

        game.addPoints(
            hashMapOf(
                host.id to 10L,
                otherId to 100L
            )
        )

        val scores = game.getAllScores()

        assertEquals(10L, scores[host.id])
        assertEquals(100L, scores[otherId])
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

    @Test
    fun computeScoreWorksAsIntended() {
        assertEquals(100, Game.computeScore(1))
        assertEquals(85, Game.computeScore(2))
        assertEquals(70, Game.computeScore(3))
        assertEquals(65, Game.computeScore(4))
        assertEquals(60, Game.computeScore(5))
        assertEquals(15, Game.computeScore(14))
        assertEquals(10, Game.computeScore(15))
        assertEquals(10, Game.computeScore(16))
        assertEquals(10, Game.computeScore(160))
    }**/
}