package com.github.fribourgsdp.radio

import android.view.View
import com.github.fribourgsdp.radio.mockimplementations.FakeGameView
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*


class PlayerGameHandlerTest {
    private lateinit var mockSnapshot: DocumentSnapshot

    private val sleepingTime = 1L
    private val singer = "Singer"
    private val song = "A good song"
    private val round = 1L
    private val listOfSongs = arrayListOf("Song0", "Song1", "Song2")

    @Before
    fun setup() {
        mockSnapshot = mock(DocumentSnapshot::class.java)
        `when`(mockSnapshot.getString("singer")).thenReturn(singer)
        `when`(mockSnapshot.exists()).thenReturn(true)
        `when`(mockSnapshot.getLong("current_round")).thenReturn(round)
        `when`(mockSnapshot.get("song_choices")).thenReturn(listOfSongs)
        `when`(mockSnapshot.getString("current_song")).thenReturn(song)
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
    fun displayGuessWhenOtherPlayer() {
        val view = FakeGameView("Not singer")
        val handler = PlayerGameHandler(0, view)

        handler.handleSnapshot(mockSnapshot)

        assertFalse(view.checkPlayer(singer))
        assertEquals(View.GONE, view.songVisibility)
        assertEquals(View.VISIBLE, view.guessInputVisibility)
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
    fun displayErrorOnDatabaseFailure() {
        val view = FakeGameView(singer)
        val db = mock(Database::class.java)
        `when`(db.updateCurrentSongOfGame(anyLong(), anyString()))
            .thenReturn(Tasks.forException(Exception()))

        val handler = PlayerGameHandler(0, view, db)

        handler.handleSnapshot(mockSnapshot)

        // Wait for the task of the database to execute
        Thread.sleep(sleepingTime)

        assertTrue(view.checkPlayer(singer))
        assertEquals("An error occurred", view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)
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
        `when`(db.setPlayerDone(anyLong(), anyString()))
            .thenReturn(Tasks.forResult(null))

        val handler = PlayerGameHandler(0, view)

        handler.handleSnapshot(mockSnapshot)
        handler.handleGuess(song, "")

        assertEquals(View.VISIBLE, view.songVisibility)
        assertEquals(song, view.song)
        assertEquals(View.GONE, view.guessInputVisibility)
    }

    @Test
    fun displayErrorOnBadGuess() {
        val view = FakeGameView("Not Singer")
        val db = mock(Database::class.java)
        `when`(db.setPlayerDone(anyLong(), anyString()))
            .thenReturn(Tasks.forResult(null))

        val handler = PlayerGameHandler(0, view)

        handler.handleSnapshot(mockSnapshot)
        handler.handleGuess("Not the song", "")

        assertEquals("Wrong answer", view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)
    }

}