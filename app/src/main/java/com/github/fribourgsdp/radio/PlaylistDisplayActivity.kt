package com.github.fribourgsdp.radio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class PlaylistDisplayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_playlist_display)
        val playlist : Playlist = Json.decodeFromString(intent.getStringExtra(PLAYLIST_DATA)!!)

        val playlistName : TextView = findViewById(R.id.PlaylistName)
        playlistName.text = playlist.name
    }
}