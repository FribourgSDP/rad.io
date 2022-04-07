package com.github.fribourgsdp.radio

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Tasks
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val SONG_DATA = "com.github.fribourgsdp.radio.SONG_INNER_DATA"
const val PLAYLIST_TO_MODIFY = "com.github.fribourgsdp.radio.PLAYLIST_TO_MODIFY"

class PlaylistSongsFragment : MyFragment(R.layout.fragment_playlist_display), OnClickListener{
    private lateinit var playlist: Playlist
    private lateinit var songs: List<Song>
    private lateinit var playlistName: String
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private lateinit var user : User

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
        //sets listeners to 2 buttons
        editButton = requireView().findViewById(R.id.editButton)
        editButton.setOnClickListener {
            val intent  = Intent(context, AddPlaylistActivity::class.java)
            intent.putExtra(PLAYLIST_TO_MODIFY, Json.encodeToString(playlist))
            startActivity(intent)
        }
        User.loadOrDefault(requireContext()).addOnSuccessListener { l ->
            user = l
        }
        deleteButton = requireView().findViewById(R.id.deleteButton)
        deleteButton.setOnClickListener{
            //val user = Tasks.await()
            //removes playlist from user playlists
            log(user)
            log(user.getPlaylists().size)
            log(playlist)
            log(user.removePlaylistByName(playlistName))
            user.save(requireContext())
            activity?.onBackPressed()
        }
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
    companion object{
        fun log(s : Any){
            Log.println(Log.ASSERT, "******* ", s.toString())
        }
    }
}