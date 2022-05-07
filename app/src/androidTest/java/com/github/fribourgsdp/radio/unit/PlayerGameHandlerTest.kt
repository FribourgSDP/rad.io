package com.github.fribourgsdp.radio.unit

import android.view.View
import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.game.handler.PlayerGameHandler
import com.github.fribourgsdp.radio.mockimplementations.FakeGameView
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.util.*


class PlayerGameHandlerTest {
    private lateinit var mockSnapshot: DocumentSnapshot

    private val sleepingTime = 50L
    private val singer = "Singer"
    private val song = "A good song"
    private val round = 1L
    private val listOfSongs = arrayListOf("Song0", "Song1", "Song2")
    private val scores = hashMapOf(
        "singer0" to 100L,
        "singer1" to 85L
    )
    private val deadline = Date(1998_000L)

    @Before
    fun setup() {
        mockSnapshot = mock(DocumentSnapshot::class.java)
        `when`(mockSnapshot.getString("singer")).thenReturn(singer)
        `when`(mockSnapshot.exists()).thenReturn(true)
        `when`(mockSnapshot.getLong("current_round")).thenReturn(round)
        `when`(mockSnapshot.get("song_choices")).thenReturn(listOfSongs)
        `when`(mockSnapshot.getString("current_song")).thenReturn(null)
        `when`(mockSnapshot.get("scores")).thenReturn(scores)
        `when`(mockSnapshot.getBoolean("finished")).thenReturn(false)
        `when`(mockSnapshot.getTimestamp("round_deadline")).thenReturn(Timestamp(deadline))
        `when`(mockSnapshot.getBoolean("validity")).thenReturn(true)

    }

    @Test
    fun correctSingerUpdate() {
        val view = FakeGameView()
        val handler = PlayerGameHandler(0, view)

        handler.handleSnapshot(mockSnapshot)

        assertEquals(singer, view.singer)
    }

    @Test
    fun correctRoundUpdate() {
        val view = FakeGameView()
        val handler = PlayerGameHandler(0, view)

        handler.handleSnapshot(mockSnapshot)

        assertEquals(round, view.round)
    }

    @Test
    fun displayGuessWhenOtherPlayerAndPickNotNull() {
        val view = FakeGameView("Not singer")
        val handler = PlayerGameHandler(0, view)
        `when`(mockSnapshot.getString("current_song")).thenReturn(song)

        handler.handleSnapshot(mockSnapshot)

        assertFalse(view.checkPlayer(singer))
        assertEquals(View.GONE, view.songVisibility)
        assertEquals(View.VISIBLE, view.guessInputVisibility)
    }

    @Test
    fun callDisplayLyricsOnSnapshot(){
        val view = FakeGameView(singer)
        val handler = PlayerGameHandler(0, view)
        `when`(mockSnapshot.getString("current_song")).thenReturn("Momentum")
        `when`(mockSnapshot.get("song_choices_lyrics")).thenReturn(hashMapOf("Momentum" to "Lorem Ipsum"))
        handler.handleSnapshot(mockSnapshot)
        assertTrue(view.lyricsDisplayed)
    }

    @Test
    fun displayWaitWhenOtherPlayerAndPickNull() {
        val view = FakeGameView("Not singer")
        val handler = PlayerGameHandler(0, view)

        handler.handleSnapshot(mockSnapshot)


        // Wait for the task of the database to execute

        assertFalse(view.checkPlayer(singer))

        assertEquals(View.VISIBLE, view.songVisibility)
        assertEquals(singer, view.song)
        assertEquals(View.GONE, view.guessInputVisibility)
    }

    @Test
    fun displaySongOnDatabaseSuccess() {
        val view = FakeGameView(singer)
        val db = mock(Database::class.java)
        `when`(db.updateCurrentSongOfGame(anyLong(), anyString()))
            .thenReturn(Tasks.forResult(null))

        val handler = PlayerGameHandler(0, view, db)

        handler.handleSnapshot(mockSnapshot)

        // Wait for the task of the database to execute
        Thread.sleep(sleepingTime)

        assertTrue(view.checkPlayer(singer))
        assertEquals(View.VISIBLE, view.songVisibility)
        assertEquals(listOfSongs[0], view.song)
        assertEquals(View.GONE, view.guessInputVisibility)
    }

    @Test
    fun displayErrorOnNullSnapshot() {
        val view = FakeGameView()

        val handler = PlayerGameHandler(0, view)

        handler.handleSnapshot(null)

        assertEquals("An error occurred", view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)
    }

    @Test
    fun displayErrorWhenSnapshotNotExists() {
        `when`(mockSnapshot.exists()).thenReturn(false)

        val view = FakeGameView()

        val handler = PlayerGameHandler(0, view)

        handler.handleSnapshot(mockSnapshot)

        assertEquals("An error occurred", view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)
    }

    @Test
    fun displaySongOnGoodGuess() {
        val view = FakeGameView("Not Singer")
        val db = mock(Database::class.java)
        `when`(db.playerEndTurn(anyLong(), anyString(), anyBoolean()))
            .thenReturn(Tasks.forResult(null))

        `when`(mockSnapshot.getString("current_song")).thenReturn(song)

        val handler = PlayerGameHandler(0, view, db)

        // Update song to guess
        handler.handleSnapshot(mockSnapshot)

        // Check it
        handler.handleGuess(song, "")

        // Wait for the task of the database to execute
        Thread.sleep(1)

        assertEquals(View.VISIBLE, view.songVisibility)
        assertEquals("You correctly guessed $song", view.song)
        assertEquals(View.GONE, view.guessInputVisibility)
    }

    @Test
    fun displayErrorOnBadGuess() {
        val view = FakeGameView("Not Singer")
        val db = mock(Database::class.java)
        `when`(db.playerEndTurn(anyLong(), anyString(), anyBoolean()))
            .thenReturn(Tasks.forResult(null))

        `when`(mockSnapshot.getString("current_song")).thenReturn(song)

        val handler = PlayerGameHandler(0, view, db)

        // Update song to guess
        handler.handleSnapshot(mockSnapshot)

        // Check it
        handler.handleGuess("Not the song", "")

        // Wait for the task of the database to execute
        Thread.sleep(1)

        assertEquals("Wrong answer", view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)
    }

    @Test
    fun displayOtherErrorWhenClose() {
        val view = FakeGameView("Not Singer")
        val db = mock(Database::class.java)
        `when`(db.playerEndTurn(anyLong(), anyString(), anyBoolean()))
            .thenReturn(Tasks.forResult(null))

        `when`(mockSnapshot.getString("current_song")).thenReturn(song)

        val handler = PlayerGameHandler(0, view, db)

        // Update song to guess
        handler.handleSnapshot(mockSnapshot)

        // Check it
        handler.handleGuess(song + "a", "")

        // Wait for the task of the database to execute
        Thread.sleep(1)

        assertEquals("You're close!", view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)
    }

    @Test
    fun displayErrorOnDatabaseFailureToEndTurn() {
        val view = FakeGameView("Not Singer")
        val db = mock(Database::class.java)
        `when`(db.playerEndTurn(anyLong(), anyString(), anyBoolean()))
            .thenReturn(Tasks.forException(Exception()))

        `when`(mockSnapshot.getString("current_song")).thenReturn(song)

        val handler = PlayerGameHandler(0, view, db)

        // Update song to guess
        handler.handleSnapshot(mockSnapshot)

        // Check it
        handler.handleGuess("Not the song", "")

        // Wait for the task of the database to execute
        Thread.sleep(1)

        assertEquals("Wrong answer", view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)
    }

    @Test
    fun onPickDisplaySongOnSuccess() {
        val view = FakeGameView()
        val db = mock(Database::class.java)
        `when`(db.updateCurrentSongOfGame(anyLong(), anyString()))
            .thenReturn(Tasks.forResult(null))

        val handler = PlayerGameHandler(0, view, db)

        handler.onPick(song)

        // Wait for the task of the database to execute
        Thread.sleep(100)

        assertEquals(View.VISIBLE, view.songVisibility)
        assertEquals(song, view.song)
        assertEquals(View.GONE, view.guessInputVisibility)
    }

    @Test
    fun onPickDisplayErrorOnDBFailure() {
        val view = FakeGameView()
        val db = mock(Database::class.java)
        `when`(db.updateCurrentSongOfGame(anyLong(), anyString()))
            .thenReturn(Tasks.forException(Exception()))

        val handler = PlayerGameHandler(0, view, db)

        handler.onPick(song)

        // Wait for the task of the database to execute
        Thread.sleep(1000)
        
        assertEquals("An error occurred", view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)
    }

    @Test
    fun displayScoresWorks() {
        val view = FakeGameView()
        val handler = PlayerGameHandler(0, view)

        handler.handleSnapshot(mockSnapshot)

        assertTrue(mapEquals(scores, view.scores))
    }

    private  fun mapEquals(first: Map<String, Long>, second: Map<String, Long>): Boolean {
        return if (first.size != second.size)  false
        else first.entries.stream()
            .allMatch { (k, v) -> v == second[k] }
    }

    @Test
    fun gameFinishedCorrectlyHandled() {
        val view = FakeGameView()
        val handler = PlayerGameHandler(0, view)

        `when`(mockSnapshot.getBoolean("finished")).thenReturn(true)

        handler.handleSnapshot(mockSnapshot)

        assertTrue(view.gameOver)
    }

    @Test
    fun timerLaunchedWhenGuessing() {
        val view = FakeGameView("Not singer")
        val handler = PlayerGameHandler(0, view)
        `when`(mockSnapshot.getString("current_song")).thenReturn(song)

        handler.handleSnapshot(mockSnapshot)

        assertTrue(view.timerRunning)
        assertEquals(deadline, view.timerDeadline)
    }

    @Test
    fun timerStoppedOnGoodGuess() {
        val view = FakeGameView("Not Singer")
        // Say the view started the timer to see the difference
        view.startTimer(deadline)

        val db = mock(Database::class.java)
        `when`(db.playerEndTurn(anyLong(), anyString(), anyBoolean()))
            .thenReturn(Tasks.forResult(null))

        `when`(mockSnapshot.getString("current_song")).thenReturn(song)

        val handler = PlayerGameHandler(0, view, db)

        // Update song to guess
        handler.handleSnapshot(mockSnapshot)

        // Check it
        handler.handleGuess(song, "")

        // Wait for the task of the database to execute
        Thread.sleep(1)

        assertFalse(view.timerRunning)
    }

    @Test
    fun hideErrorOnTimeout() {
        val view = FakeGameView("Not Singer")
        val db = mock(Database::class.java)
        `when`(db.playerEndTurn(anyLong(), anyString(), anyBoolean()))
            .thenReturn(Tasks.forResult(null))

        `when`(mockSnapshot.getString("current_song")).thenReturn(song)

        val handler = PlayerGameHandler(0, view, db)

        // Update song to guess
        handler.handleSnapshot(mockSnapshot)

        // Check it
        handler.handleGuess("", "", true)

        // Wait for the task of the database to execute
        Thread.sleep(1)

        assertEquals(View.GONE, view.errorVisibility)
    }

}