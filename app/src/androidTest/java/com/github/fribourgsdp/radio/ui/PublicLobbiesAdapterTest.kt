package com.github.fribourgsdp.radio.ui

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.github.fribourgsdp.radio.data.LobbyData
import com.github.fribourgsdp.radio.data.LobbyDataKeys
import com.github.fribourgsdp.radio.game.view.PublicLobbiesAdapter
import com.github.fribourgsdp.radio.mockimplementations.LocalDatabase
import org.junit.Assert.assertEquals
import org.junit.Test

class PublicLobbiesAdapterTest {
    private val ctx: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun sortByWorks() {
        val db = LocalDatabase()
        val adapter = PublicLobbiesAdapter(ctx, db)

        val assertForEach: (Int, LobbyData) -> Unit = { index, lobbyData ->
            assertEquals(lobbyData.id, adapter.getItemId(index))
        }

        // id check
        adapter.sortBy(LobbyDataKeys.ID)
        Thread.sleep(100)
        db.lobbies.sortedBy { it.id }
            .forEachIndexed(assertForEach)

        // name check
        adapter.sortBy(LobbyDataKeys.NAME)
        Thread.sleep(100)
        db.lobbies.sortedBy { it.name }
            .forEachIndexed(assertForEach)

        // hostname check
        adapter.sortBy(LobbyDataKeys.HOSTNAME)
        Thread.sleep(100)
        db.lobbies.sortedBy { it.hostName }
            .forEachIndexed(assertForEach)
    }
}