package com.github.fribourgsdp.radio.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
//import com.github.fribourgsdp.radio.PLAYLIST_DATA
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.backend.SongAdapter
import com.github.fribourgsdp.radio.backend.Playlist
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class PlaylistDisplayActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_playlist_display)
        val playlist : Playlist = Json.decodeFromString(intent.getStringExtra(PLAYLIST_DATA)!!)

        val playlistName : TextView = findViewById(R.id.PlaylistName)
        playlistName.text = playlist.name

        val songs = playlist.getSongs().toList()
        val songDisplay : RecyclerView = findViewById(R.id.SongRecyclerView)
        songDisplay.adapter = SongAdapter(songs)
        songDisplay.layoutManager = (LinearLayoutManager(this))
        songDisplay.setHasFixedSize(true)
    }
}