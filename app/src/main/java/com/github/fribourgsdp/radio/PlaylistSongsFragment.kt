package com.github.fribourgsdp.radio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class PlaylistSongsFragment : Fragment(){
    private lateinit var songs: List<Song>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            args.getString(PLAYLIST_DATA).let { serializedPlaylist ->
                val playlist: Playlist = Json.decodeFromString(serializedPlaylist!!)
                songs = playlist.getSongs().toList()
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_playlist_display, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeRecyclerView()
    }

    private fun initializeRecyclerView() {
        val songDisplay : RecyclerView = requireView().findViewById(R.id.SongRecyclerView)
        songDisplay.adapter = SongAdapter(songs)
        songDisplay.layoutManager = (LinearLayoutManager(activity))
        songDisplay.setHasFixedSize(true)
    }
}