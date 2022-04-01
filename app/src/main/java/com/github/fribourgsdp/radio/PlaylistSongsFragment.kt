package com.github.fribourgsdp.radio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val SONG_DATA = "com.github.fribourgsdp.radio.SONG_INNER_DATA"

class PlaylistSongsFragment : MyFragment(R.layout.fragment_playlist_display), OnClickListener{
    private lateinit var playlist: Playlist
    private lateinit var songs: List<Song>
    private lateinit var playlistName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            args.getString(PLAYLIST_DATA).let { serializedPlaylist ->
                playlist = Json.decodeFromString(serializedPlaylist!!)
                songs = playlist.getSongs().toList()
                playlistName = playlist.name
            }
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val playlistTitle : TextView = requireView().findViewById(R.id.PlaylistName)
        playlistTitle.text = playlistName
        initializeRecyclerView()
    }

    private fun initializeRecyclerView() {
        val songDisplay : RecyclerView = requireView().findViewById(R.id.SongRecyclerView)
        songDisplay.adapter = SongAdapter(songs, this)
        songDisplay.layoutManager = (LinearLayoutManager(activity))
        songDisplay.setHasFixedSize(true)
    }

    override fun onItemClick(position: Int) {
        val bundle = Bundle()
        bundle.putString(PLAYLIST_DATA, Json.encodeToString(playlist))
        bundle.putString(SONG_DATA, Json.encodeToString(songs[position]))
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.container, SongFragment::class.java, bundle)
            ?.addToBackStack("SongFragment")
            ?.commit()
    }
}