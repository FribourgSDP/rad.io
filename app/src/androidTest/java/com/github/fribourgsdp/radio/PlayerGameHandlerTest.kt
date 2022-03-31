package com.github.fribourgsdp.radio

import android.view.View
import com.github.fribourgsdp.radio.mockimplementations.LocalDatabase
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.rules.Timeout
import org.mockito.Mockito.*


class PlayerGameHandlerTest {
    private lateinit var mockSnapshot: DocumentSnapshot

    private val singer = "Singer"
    private val round = 1L
    private val listOfSongs = arrayListOf("Song0", "Song1", "Song2")

    @Before
    fun setup() {
        mockSnapshot = mock(DocumentSnapshot::class.java)
        `when`(mockSnapshot.getString("singer")).thenReturn(singer)
        `when`(mockSnapshot.exists()).thenReturn(true)
        `when`(mockSnapshot.getLong("current_round")).thenReturn(round)
        `when`(mockSnapshot.get("song_choices")).thenReturn(listOfSongs)
    }

    @Test
    fun correctSingerUpdate() {
        val view = FakeView()
        val handler = PlayerGameHandler(0, view)

        handler.handleSnapshot(mockSnapshot)

        assertEquals(singer, view.singer)
    }

    @Test
    fun correctRoundUpdate() {
        val view = FakeView()
        val handler = PlayerGameHandler(0, view)

        handler.handleSnapshot(mockSnapshot)

        assertEquals(round, view.round)
    }

    @Test
    fun displayGuessWhenOtherPlayer() {
        val view = FakeView("Not singer")
        val handler = PlayerGameHandler(0, view)

        handler.handleSnapshot(mockSnapshot)

        assertFalse(view.checkPlayer(singer))
        assertEquals(View.GONE, view.songVisibility)
        assertEquals(View.VISIBLE, view.guessInputVisibility)
    }

    @Test
    fun displaySongOnDatabaseSuccess() {
        val view = FakeView(singer)
        val db = mock(Database::class.java)
        `when`(db.updateCurrentSongOfGame(anyLong(), anyString()))
            .thenReturn(Tasks.forResult(null))

        val handler = PlayerGameHandler(0, view, db)

        handler.handleSnapshot(mockSnapshot)

        // Wait for the task of the database to execute
        Thread.sleep(1000)

        assertTrue(view.checkPlayer(singer))
        assertEquals(View.VISIBLE, view.songVisibility)
        assertEquals(listOfSongs[0], view.song)
        assertEquals(View.GONE, view.guessInputVisibility)
    }

    @Test
    fun displayErrorOnDatabaseFailure() {
        val view = FakeView(singer)
        val db = mock(Database::class.java)
        `when`(db.updateCurrentSongOfGame(anyLong(), anyString()))
            .thenReturn(Tasks.forException(Exception()))

        val handler = PlayerGameHandler(0, view, db)

        handler.handleSnapshot(mockSnapshot)

        // Wait for the task of the database to execute
        Thread.sleep(1000)

        assertTrue(view.checkPlayer(singer))
        assertEquals("An error occurred", view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)
    }

    @Test
    fun displayErrorOnNullSnapshot() {
        val view = FakeView()

        val handler = PlayerGameHandler(0, view)

        handler.handleSnapshot(null)

        assertEquals("An error occurred", view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)
    }

    @Test
    fun displayErrorWhenSnapshotNotExists() {
        `when`(mockSnapshot.exists()).thenReturn(false)

        val view = FakeView()

        val handler = PlayerGameHandler(0, view)

        handler.handleSnapshot(mockSnapshot)

        assertEquals("An error occurred", view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)
    }

}

private class FakeView(private val playerID: String = ""): GameView {

    var singer = ""
    var round = 0L

    var song = ""
    var songVisibility = View.GONE

    var error = ""
    var errorVisibility= View.GONE

    var guessInputVisibility = View.VISIBLE

    override fun chooseSong(choices: List<String>): String {
        return choices[0]
    }

    override fun updateSinger(singerName: String) {
        singer = singerName
    }

    override fun updateRound(currentRound: Long) {
        round = currentRound
    }

    override fun displaySong(songName: String) {
        guessInputVisibility = View.GONE
        song = songName
        songVisibility = View.VISIBLE
    }

    override fun displayGuessInput() {
        songVisibility = View.GONE
        guessInputVisibility = View.VISIBLE
    }

    override fun displayError(errorMessage: String) {
        error = errorMessage
        errorVisibility = View.VISIBLE
    }

    override fun checkPlayer(id: String): Boolean {
        return playerID == id
    }

}