package com.github.fribourgsdp.radio

import android.view.View
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.game.Game
import com.github.fribourgsdp.radio.game.handler.HostGameHandler
import com.github.fribourgsdp.radio.mockimplementations.FakeGameView
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class HostGameHandlerTest {
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
        `when`(mockSnapshot.exists()).thenReturn(true)
    }

    @Test
    fun displayErrorOnDatabaseUpdateFailure() {
        val view = FakeGameView()
        val db = mock(Database::class.java)
        `when`(db.updateGame(anyLong(), anyMap()))
            .thenReturn(Tasks.forException(Exception()))

        val handler = HostGameHandler(fakeGame, view, db)

        handler.handleSnapshot(mockSnapshot)

        // Wait for the task of the database to execute
        Thread.sleep(sleepingTime)

        assertEquals("An error occurred.", view.error)
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

        val handler = HostGameHandler(fakeGame, view, db)

        handler.handleSnapshot(mockSnapshot)

        // Wait for the task of the database to execute
        Thread.sleep(sleepingTime)

        assertEquals("An error occurred.", view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)
    }

    @Test
    fun displayErrorOnNullSnapshot() {
        val view = FakeGameView()

        val handler = HostGameHandler(fakeGame, view)

        handler.handleSnapshot(null)

        assertEquals("An error occurred.", view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)
    }

    @Test
    fun displayErrorWhenSnapshotNotExists() {
        `when`(mockSnapshot.exists()).thenReturn(false)

        val view = FakeGameView()

        val handler = HostGameHandler(fakeGame, view)

        handler.handleSnapshot(mockSnapshot)

        assertEquals("An error occurred.", view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)
    }
}