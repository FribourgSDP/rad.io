package com.github.fribourgsdp.radio.unit

import android.content.Context
import android.view.View
import androidx.test.core.app.ApplicationProvider
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.data.Genre
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.game.Game
import com.github.fribourgsdp.radio.game.handler.HostGameHandler
import com.github.fribourgsdp.radio.mockimplementations.FakeGameView
import com.github.fribourgsdp.radio.utils.testLyrics4
import com.github.fribourgsdp.radio.utils.testPlaylist4
import com.github.fribourgsdp.radio.utils.testSong4
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class HostGameHandlerTest {
    private val ctx: Context = ApplicationProvider.getApplicationContext()

    private val sleepingTime = 50L
    private lateinit var mockSnapshot: DocumentSnapshot

    private val host = User("Some player").apply {
        id = "some_player_id"
    }
    private val otherPlayer = User("Another Player").apply {
        id = "another_player_id"
    }
    private val fakeGame = Game.Builder()
        .setHost(host)
        .addUserId(otherPlayer.id)
        .setPlaylist(Playlist("playlist"))
        .build()

    @Before
    fun setup() {
        mockSnapshot = mock(DocumentSnapshot::class.java)
        `when`(mockSnapshot.get("player_done_map")).thenReturn(fakeGame.getAllPlayersId().associateWith { true })
        `when`(mockSnapshot.get("scores_of_round")).thenReturn(fakeGame.getAllPlayersId().associateWith { 0L })
        `when`(mockSnapshot.get("player_found_map")).thenReturn(fakeGame.getAllPlayersId().associateWith { true })
        `when`(mockSnapshot.get("current_song")).thenReturn("Test Song")
        `when`(mockSnapshot.exists()).thenReturn(true)
    }

    @Test
    fun displayErrorOnDatabaseUpdateFailure() {
        val view = FakeGameView()
        val db = mock(Database::class.java)
        `when`(db.updateGame(anyLong(), anyMap()))
            .thenReturn(Tasks.forException(Exception()))

        val handler = HostGameHandler(ctx, fakeGame, view, db)

        handler.handleSnapshot(mockSnapshot)

        // Wait for the task of the database to execute
        Thread.sleep(sleepingTime)

        assertEquals(ctx.getString(R.string.game_error), view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)
    }

    @Test
    fun displayErrorOnDatabaseResetFailure() {
        val view = FakeGameView()
        val db = mock(Database::class.java)
        `when`(db.updateGame(anyLong(), anyMap()))
            .thenReturn(Tasks.forResult(null))
        `when`(db.resetGameMetadata(anyLong(), anyString()))
            .thenReturn(Tasks.forException(Exception()))

        val handler = HostGameHandler(ctx, fakeGame, view, db)

        handler.handleSnapshot(mockSnapshot)

        // Wait for the task of the database to execute
        Thread.sleep(sleepingTime)

        assertEquals(ctx.getString(R.string.game_error), view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)
    }

    @Test
    fun displayErrorOnNullSnapshot() {
        val view = FakeGameView()

        val handler = HostGameHandler(ctx, fakeGame, view)

        handler.handleSnapshot(null)

        assertEquals(ctx.getString(R.string.game_error), view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)
    }

    @Test
    fun displayErrorWhenSnapshotNotExists() {
        `when`(mockSnapshot.exists()).thenReturn(false)

        val view = FakeGameView()

        val handler = HostGameHandler(ctx, fakeGame, view)

        handler.handleSnapshot(mockSnapshot)

        assertEquals(ctx.getString(R.string.game_error), view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)
    }

    @Test
    fun gameCrashOnDoubleDatabaseUpdateFailure() {
        val view = FakeGameView()
        val db = mock(Database::class.java)
        `when`(db.updateGame(anyLong(), anyMap()))
            .thenReturn(Tasks.forException(Exception()))

        val handler = HostGameHandler(ctx, fakeGame, view, db)

        handler.handleSnapshot(mockSnapshot)

        // Wait for the task of the database to execute
        Thread.sleep(sleepingTime)

        assertEquals(ctx.getString(R.string.game_error), view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)
        assertTrue(view.gameOver)
        assertTrue(view.crashed)
    }



    @Test
    fun gameCrashOnDoubleDatabaseResetFailure() {
        val view = FakeGameView()
        val db = mock(Database::class.java)
        `when`(db.updateGame(anyLong(), anyMap()))
            .thenReturn(Tasks.forResult(null))
        `when`(db.resetGameMetadata(anyLong(), anyString()))
            .thenReturn(Tasks.forException(Exception()))

        val handler = HostGameHandler(ctx, fakeGame, view, db)

        handler.handleSnapshot(mockSnapshot)

        // Wait for the task of the database to execute
        Thread.sleep(sleepingTime)

        assertEquals(ctx.getString(R.string.game_error), view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)
        assertTrue(view.gameOver)
        assertTrue(view.crashed)
    }

    @Test
    fun incrementationOfRoundTest(){
        val view = FakeGameView()
        val game = Game.Builder()
            .setNoSing(true)
            .setHost(host)
            .addUserId(otherPlayer.id)
            .setPlaylist(Playlist("Test PL", setOf(Song("a", "b", "c")), Genre.NONE))
            .build()
        val db = mock(Database::class.java)
        `when`(db.updateGame(anyLong(), anyMap()))
            .thenReturn(Tasks.forResult(null))
        `when`(db.resetGameMetadata(anyLong(), anyString()))
            .thenReturn(Tasks.forResult(null))
        `when`(db.updateCurrentSongOfGame(anyLong(), anyString(), anyLong()))
            .thenReturn(Tasks.forResult(null))
        val handler = HostGameHandler(ctx, game, view, db, noSing = true)
        handler.handleSnapshot(mockSnapshot)

        // Wait for the task of the database to execute
        Thread.sleep(sleepingTime)

        assertEquals(2, game.currentRound)
    }

    @Test
    fun assignSongDisplaysErrorOnDBError(){
        val view = FakeGameView()
        val game = Game.Builder()
            .setNoSing(true)
            .setHost(host)
            .addUserId(otherPlayer.id)
            .setPlaylist(Playlist(testPlaylist4, setOf(Song(testSong4, "", testLyrics4)), Genre.NONE))
            .build()
        val db = mock(Database::class.java)
        `when`(db.updateGame(anyLong(), anyMap()))
            .thenReturn(Tasks.forResult(null))
        `when`(db.resetGameMetadata(anyLong(), anyString()))
            .thenReturn(Tasks.forResult(null))
        `when`(db.updateCurrentSongOfGame(anyLong(), anyString(), anyLong()))
            .thenReturn(Tasks.forException(Exception()))
        val handler = HostGameHandler(ctx, game, view, db, noSing = true)

        handler.handleSnapshot(mockSnapshot)

        // Wait for the task of the database to execute
        Thread.sleep(sleepingTime)

        assertEquals(view.error, ctx.getString(R.string.game_error))
    }

    @Test
    fun updateMapContainsSongChoices(){
        val view = FakeGameView()
        val game = Game.Builder()
            .setNoSing(false)
            .setHost(host)
            .addUserId(otherPlayer.id)
            .setPlaylist(Playlist("Test PL", setOf(
                Song("a", "b", "c"),
                Song("d", "e", "f")
            ), Genre.NONE))
            .build()
        var choices : List<String> = listOf()
        val db = mock(Database::class.java)
        `when`(db.resetGameMetadata(anyLong(), anyString()))
            .thenReturn(Tasks.forResult(null))
        `when`(db.updateGame(anyLong(), anyMap()))
            .then{i ->
                val updatesMap = i.getArgument<Map<String, Any>>(1)
                choices = ((updatesMap["song_choices"] as List<String>?)!!)
                Tasks.forResult(null)
            }
        val handler = HostGameHandler(ctx, game, view, db, noSing = false)
        handler.handleSnapshot(mockSnapshot)

        assertTrue("$choices", choices.containsAll(
            listOf("A", "D")))
    }
}