package com.github.fribourgsdp.radio.mockimplementations

import com.github.fribourgsdp.radio.data.Genre
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.data.view.AddPlaylistActivity
import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.utils.KotlinAny
import com.google.android.gms.tasks.Tasks
import org.mockito.Mockito

class MockAddPlaylistActivity : AddPlaylistActivity(){

    override fun initializeDatabase(): Database {
        val db = Mockito.mock(Database::class.java)
        Mockito.`when`(db.setUser(Mockito.anyString(),KotlinAny.any())).thenReturn(Tasks.forResult(null))
        return db
    }
}