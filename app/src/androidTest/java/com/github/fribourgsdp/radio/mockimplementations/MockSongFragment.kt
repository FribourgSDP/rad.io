package com.github.fribourgsdp.radio.mockimplementations

import com.github.fribourgsdp.radio.external.musixmatch.LyricsGetter
import com.github.fribourgsdp.radio.data.view.SongFragment
import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.utils.KotlinAny
import com.google.android.gms.tasks.Tasks
import org.mockito.Mockito
import org.mockito.Mockito.`when`

class MockSongFragment : SongFragment() {
    override fun getLyricsGetter() : LyricsGetter {
        return MockLyricsGetter
    }

    override fun initializeDatabase(): Database {
        val db = Mockito.mock(Database::class.java)
        `when`(db.registerSong(KotlinAny.any())).thenReturn(Tasks.forResult(null))
        return db
    }
}