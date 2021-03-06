package com.github.fribourgsdp.radio.unit

import android.content.Context
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.database.*
import com.github.fribourgsdp.radio.utils.testLyrics4
import com.github.fribourgsdp.radio.utils.testSong4
import com.github.fribourgsdp.radio.utils.testSong6
import com.github.fribourgsdp.radio.game.handler.PlayerGameHandler
import com.github.fribourgsdp.radio.mockimplementations.FakeGameView
import com.github.fribourgsdp.radio.utils.testLyrics6
import com.github.fribourgsdp.radio.util.MyTextToSpeech
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentSnapshot
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import java.util.*


class PlayerGameHandlerTest {
    private val ctx: Context = ApplicationProvider.getApplicationContext()

    private lateinit var mockSnapshot: DocumentSnapshot

    private val sleepingTime = 50L
    private val singer = "Singer"
    private val notSinger = "Not singer"
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
        `when`(mockSnapshot.getString(SINGER_KEY)).thenReturn(singer)
        `when`(mockSnapshot.exists()).thenReturn(true)
        `when`(mockSnapshot.getLong(CURRENT_ROUND_KEY)).thenReturn(round)
        `when`(mockSnapshot.get(SONG_CHOICES_KEY)).thenReturn(listOfSongs)
        `when`(mockSnapshot.getString(CURRENT_SONG_KEY)).thenReturn(null)
        `when`(mockSnapshot.get(SCORES_KEY)).thenReturn(scores)
        `when`(mockSnapshot.getBoolean(FINISHED_KEY)).thenReturn(false)
        `when`(mockSnapshot.getTimestamp(ROUND_DEADLINE_KEY)).thenReturn(Timestamp(deadline))
        `when`(mockSnapshot.getBoolean(VALIDITY_KEY)).thenReturn(true)

    }

    @Test
    fun correctSingerUpdate() {
        val view = FakeGameView()
        val handler = PlayerGameHandler(ctx, 0, view)

        handler.handleSnapshot(mockSnapshot)

        assertEquals(singer, view.singer)
    }

    @Test
    fun correctRoundUpdate() {
        val view = FakeGameView()
        val handler = PlayerGameHandler(ctx, 0, view)

        handler.handleSnapshot(mockSnapshot)

        assertEquals(round, view.round)
    }

    @Test
    fun displayGuessWhenOtherPlayerAndPickNotNull() {
        val view = FakeGameView(notSinger)
        val handler = PlayerGameHandler(ctx, 0, view)
        `when`(mockSnapshot.getString(CURRENT_SONG_KEY)).thenReturn(song)

        handler.handleSnapshot(mockSnapshot)

        assertFalse(view.checkPlayer(singer))
        assertEquals(View.GONE, view.songVisibility)
        assertEquals(View.VISIBLE, view.guessInputVisibility)
    }

    @Test
    fun callDisplayLyricsOnSnapshot(){
        val view = FakeGameView(singer)
        val handler = PlayerGameHandler(ctx, 0, view)
        val song = testSong6
        val lyrics = testLyrics6
        `when`(mockSnapshot.getString(CURRENT_SONG_KEY)).thenReturn(song)
        `when`(mockSnapshot.get(SONG_CHOICES_LYRICS_KEY)).thenReturn(hashMapOf(song to lyrics))
        handler.handleSnapshot(mockSnapshot)
        assertEquals(lyrics, view.lyricsDisplayed)
    }

    @Test
    fun displayWaitWhenOtherPlayerAndPickNull() {
        val view = FakeGameView(notSinger)
        val handler = PlayerGameHandler(ctx, 0, view)

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
        `when`(db.updateCurrentSongOfGame(anyLong(), anyString(), anyLong()))
            .thenReturn(Tasks.forResult(null))

        val handler = PlayerGameHandler(ctx, 0, view, db)

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

        val handler = PlayerGameHandler(ctx, 0, view)

        handler.handleSnapshot(null)

        assertEquals(ctx.getString(R.string.game_error), view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)
    }

    @Test
    fun displayErrorWhenSnapshotNotExists() {
        `when`(mockSnapshot.exists()).thenReturn(false)

        val view = FakeGameView()

        val handler = PlayerGameHandler(ctx, 0, view)

        handler.handleSnapshot(mockSnapshot)

        assertEquals(ctx.getString(R.string.game_error), view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)
    }

    @Test
    fun displaySongOnGoodGuess() {
        val view = FakeGameView(notSinger)
        val db = mock(Database::class.java)
        `when`(db.playerEndTurn(anyLong(), anyString(), anyBoolean()))
            .thenReturn(Tasks.forResult(null))

        `when`(mockSnapshot.getString(CURRENT_SONG_KEY)).thenReturn(song)

        val handler = PlayerGameHandler(ctx, 0, view, db)

        // Update song to guess
        handler.handleSnapshot(mockSnapshot)

        // Check it
        handler.handleGuess(song, "")

        // Wait for the task of the database to execute
        Thread.sleep(sleepingTime)

        assertEquals(View.VISIBLE, view.songVisibility)
        assertEquals(ctx.getString(R.string.correct_guess_format, song), view.song)
        assertEquals(View.GONE, view.guessInputVisibility)
    }

    @Test
    fun displayErrorOnBadGuess() {
        val view = FakeGameView(notSinger)
        val db = mock(Database::class.java)
        `when`(db.playerEndTurn(anyLong(), anyString(), anyBoolean()))
            .thenReturn(Tasks.forResult(null))

        `when`(mockSnapshot.getString(CURRENT_SONG_KEY)).thenReturn(song)

        val handler = PlayerGameHandler(ctx, 0, view, db)

        // Update song to guess
        handler.handleSnapshot(mockSnapshot)

        // Check it
        handler.handleGuess("Not the song", "")

        // Wait for the task of the database to execute
        Thread.sleep(sleepingTime)

        assertEquals(ctx.getString(R.string.wrong_guess), view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)
    }

    @Test
    fun displayOtherErrorWhenClose() {
        val view = FakeGameView(notSinger)
        val db = mock(Database::class.java)
        `when`(db.playerEndTurn(anyLong(), anyString(), anyBoolean()))
            .thenReturn(Tasks.forResult(null))

        `when`(mockSnapshot.getString(CURRENT_SONG_KEY)).thenReturn(song)

        val handler = PlayerGameHandler(ctx, 0, view, db)

        // Update song to guess
        handler.handleSnapshot(mockSnapshot)

        // Check it
        handler.handleGuess(song + "a", "")

        // Wait for the task of the database to execute
        Thread.sleep(sleepingTime)

        assertEquals(ctx.getString(R.string.close_guess), view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)
    }

    @Test
    fun displayErrorOnDatabaseFailureToEndTurn() {
        val view = FakeGameView(notSinger)
        val db = mock(Database::class.java)
        `when`(db.playerEndTurn(anyLong(), anyString(), anyBoolean()))
            .thenReturn(Tasks.forException(Exception()))

        `when`(mockSnapshot.getString(CURRENT_SONG_KEY)).thenReturn(song)

        val handler = PlayerGameHandler(ctx, 0, view, db)

        // Update song to guess
        handler.handleSnapshot(mockSnapshot)

        // Check it
        handler.handleGuess(song, "")

        // Wait for the task of the database to execute
        Thread.sleep(sleepingTime)

        assertEquals(ctx.getString(R.string.game_error), view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)

        // Game crashed
        assertTrue(view.gameOver)
        assertTrue(view.crashed)
    }

    @Test
    fun onPickDisplaySongOnSuccess() {
        val view = FakeGameView()
        val db = mock(Database::class.java)
        `when`(db.updateCurrentSongOfGame(anyLong(), anyString(), anyLong()))
            .thenReturn(Tasks.forResult(null))

        val handler = PlayerGameHandler(ctx, 0, view, db)

        handler.onPick(song)

        // Wait for the task of the database to execute
        Thread.sleep(sleepingTime)

        assertEquals(View.VISIBLE, view.songVisibility)
        assertEquals(song, view.song)
        assertEquals(View.GONE, view.guessInputVisibility)
    }

    @Test
    fun onPickDisplayErrorOnDBFailure() {
        val view = FakeGameView()
        val db = mock(Database::class.java)
        println("all fine 1")
        `when`(db.updateCurrentSongOfGame(anyLong(), anyString(), anyLong()))
            .thenReturn(Tasks.forException(Exception()))
        

        val handler = PlayerGameHandler(ctx, 0, view, db)

        handler.onPick(song)

        // Wait for the task of the database to execute
        Thread.sleep(sleepingTime)
        
        assertEquals(ctx.getString(R.string.game_error), view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)

        // Game crashed
        assertTrue(view.gameOver)
        assertTrue(view.crashed)
    }

    @Test
    fun displayScoresWorks() {
        val view = FakeGameView()
        val handler = PlayerGameHandler(ctx, 0, view)

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
        val handler = PlayerGameHandler(ctx, 0, view)

        `when`(mockSnapshot.getBoolean(FINISHED_KEY)).thenReturn(true)

        handler.handleSnapshot(mockSnapshot)

        assertTrue(view.gameOver)
        assertFalse(view.crashed)
    }

    @Test
    fun timerLaunchedWhenGuessing() {
        val view = FakeGameView(notSinger)
        val handler = PlayerGameHandler(ctx, 0, view)
        `when`(mockSnapshot.getString(CURRENT_SONG_KEY)).thenReturn(song)

        handler.handleSnapshot(mockSnapshot)

        assertTrue(view.timerRunning)
        assertEquals(deadline, view.timerDeadline)
    }

    @Test
    fun timerStoppedOnGoodGuess() {
        val view = FakeGameView(notSinger)
        // Say the view started the timer to see the difference
        view.startTimer(deadline)

        val db = mock(Database::class.java)
        `when`(db.playerEndTurn(anyLong(), anyString(), anyBoolean()))
            .thenReturn(Tasks.forResult(null))

        `when`(mockSnapshot.getString(CURRENT_SONG_KEY)).thenReturn(song)

        val handler = PlayerGameHandler(ctx, 0, view, db)

        // Update song to guess
        handler.handleSnapshot(mockSnapshot)

        // Check it
        handler.handleGuess(song, "")

        // Wait for the task of the database to execute
        Thread.sleep(sleepingTime)

        assertFalse(view.timerRunning)
    }

    @Test
    fun hideErrorOnTimeout() {
        val view = FakeGameView(notSinger)
        val db = mock(Database::class.java)
        `when`(db.playerEndTurn(anyLong(), anyString(), anyBoolean()))
            .thenReturn(Tasks.forResult(null))

        `when`(mockSnapshot.getString(CURRENT_SONG_KEY)).thenReturn(song)

        val handler = PlayerGameHandler(ctx, 0, view, db)

        // Update song to guess
        handler.handleSnapshot(mockSnapshot)

        // Check it
        handler.handleGuess("", "", true)

        // Wait for the task of the database to execute
        Thread.sleep(sleepingTime)

        assertEquals(View.GONE, view.errorVisibility)
    }

    @Test
    fun gameCrashOnTimeoutButDBFail() {
        val view = FakeGameView(notSinger)
        val db = mock(Database::class.java)
        `when`(db.playerEndTurn(anyLong(), anyString(), anyBoolean()))
            .thenReturn(Tasks.forException(Exception()))

        `when`(mockSnapshot.getString(CURRENT_SONG_KEY)).thenReturn(song)

        val handler = PlayerGameHandler(ctx, 0, view, db)

        // Update song to guess
        handler.handleSnapshot(mockSnapshot)

        // Check it
        handler.handleGuess("", "", true)

        // Wait for the task of the database to execute
        Thread.sleep(sleepingTime)

        // Game crashed
        assertTrue(view.gameOver)
        assertTrue(view.crashed)
    }

    @Test
    fun gameCrashedOnGameNotValid() {
        val view = FakeGameView()
        val handler = PlayerGameHandler(ctx, 0, view)

        `when`(mockSnapshot.getBoolean(VALIDITY_KEY)).thenReturn(false)

        handler.handleSnapshot(mockSnapshot)

        assertTrue(view.gameOver)
        assertTrue(view.crashed)
    }

    @Test
    fun displayPreviousSongOnTimeoutNoSingMode(){
        val view = FakeGameView()
        val tts = object : MyTextToSpeech(ctx) {
            override fun readLyrics(lyrics : String){

            }
        }
        val handler = PlayerGameHandler(ctx, 0, view, noSing = true, tts = tts)
        `when`(mockSnapshot.getString(CURRENT_SONG_KEY))
            .thenReturn(testSong4)
        `when`(mockSnapshot.get(SONG_CHOICES_LYRICS_KEY))
            .thenReturn(mapOf(testSong4 to testLyrics4))
        handler.handleSnapshot(mockSnapshot)
        handler.handleGuess("", "", timeout = true)
        assertEquals(view.song, ctx.getString(R.string.previousSongDisplay) + testSong4)
    }

}