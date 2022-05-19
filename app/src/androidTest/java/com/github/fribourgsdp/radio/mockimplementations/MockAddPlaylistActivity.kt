package com.github.fribourgsdp.radio.mockimplementations

import com.github.fribourgsdp.radio.data.Genre
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.data.view.AddPlaylistActivity
import com.github.fribourgsdp.radio.database.Database
import com.google.android.gms.tasks.Tasks
import org.mockito.Mockito

class MockAddPlaylistActivity : AddPlaylistActivity(){

    override fun initializeDatabase(): Database {
        val db = Mockito.mock(Database::class.java)
        Mockito.`when`(db.setUser(Mockito.anyString(),any())).thenReturn(Tasks.forResult(null))
        return db
    }

    //this is usefull in order to be able to use any() from mockito
    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }
    private fun <T> uninitialized(): T = null as T
}