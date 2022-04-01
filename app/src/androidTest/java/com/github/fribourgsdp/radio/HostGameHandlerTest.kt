package com.github.fribourgsdp.radio

import android.view.View
import com.github.fribourgsdp.radio.mockimplementations.FakeGameView
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*

class HostGameHandlerTest {
    private lateinit var mockSnapshot: DocumentSnapshot

    private val doneMap = hashMapOf("Some player" to true, "Another player" to true)
    private val fakeGame = Game.Builder()
        .setHost(User("host"))
        .setPlaylist(Playlist("playlist"))
        .build()

    @Before
    fun setup() {
        mockSnapshot = mock(DocumentSnapshot::class.java)
        `when`(mockSnapshot.get("player_done_map")).thenReturn(doneMap)
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
        Thread.sleep(1000)

        assertEquals("An error occurred.", view.error)
        assertEquals(View.VISIBLE, view.errorVisibility)
    }

    @Test
    fun displayErrorOnDatabaseResetFailure() {
        val view = FakeGameView()
        val db = mock(Database::class.java)
        `when`(db.updateGame(anyLong(), anyMap()))
            .thenReturn(Tasks.forResult(null))
        `when`(db.resetPlayerDoneMap(anyLong(), anyString()))
            .thenReturn(Tasks.forException(Exception()))

        val handler = HostGameHandler(fakeGame, view, db)

        handler.handleSnapshot(mockSnapshot)

        // Wait for the task of the database to execute
        Thread.sleep(1000)

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