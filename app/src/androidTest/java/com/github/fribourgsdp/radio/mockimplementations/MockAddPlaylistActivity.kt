package com.github.fribourgsdp.radio.mockimplementations

import android.content.Context
import android.os.Bundle
import com.github.fribourgsdp.radio.data.Genre
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.data.view.AddPlaylistActivity
import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.utils.KotlinAny
import com.google.android.gms.tasks.Tasks
import org.mockito.Mockito

class MockAddPlaylistActivity : AddPlaylistActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        User.loadOrDefault(this).addOnSuccessListener { u ->
            user = u
            user.isGoogleUser = true

        }
    }
    override fun initializeDatabase(): Database {
        val db = Mockito.mock(Database::class.java)
        Mockito.`when`(db.setUser(Mockito.anyString(),KotlinAny.any())).thenReturn(Tasks.forResult(null))
        return db
    }
}
class MockAddPlaylistActivityNoConnection : AddPlaylistActivity(){

    override fun initializeDatabase(): Database {
        val db = Mockito.mock(Database::class.java)
        return db
    }

    override fun hasConnectivity(context: Context): Boolean {
        return false
    }
}