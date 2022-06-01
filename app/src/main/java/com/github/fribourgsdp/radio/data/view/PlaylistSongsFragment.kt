package com.github.fribourgsdp.radio.data.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.fribourgsdp.radio.*
import com.github.fribourgsdp.radio.config.ConnectivityChecker
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.database.DatabaseHolder
import com.github.fribourgsdp.radio.util.MyFragment
import com.github.fribourgsdp.radio.util.OnClickListener
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val SONG_DATA = "com.github.fribourgsdp.radio.SONG_INNER_DATA"
const val SONG_NAME_INDEX = 0
const val SONG_ARTIST_INDEX = 1
const val PLAYLIST_TO_MODIFY = "com.github.fribourgsdp.radio.data.view.PLAYLIST_TO_MODIFY"
const val ADD_PLAYLIST_FLAG = "com.github.fribourgsdp.radio.data.view.ADD_PLAYLIST_FLAG"

open class PlaylistSongsFragment : MyFragment(R.layout.fragment_playlist_display),
    ConnectivityChecker,OnClickListener,
    DatabaseHolder {
    private lateinit var playlist: Playlist
    private lateinit var songs: List<Song>
    private lateinit var playlistName: String
    private lateinit var editButton: ImageButton
    private lateinit var deleteButton: ImageButton
    private lateinit var saveOnlineButton : ImageButton
    private lateinit var importLyricsButton : Button
    private lateinit var user : User
    
    var db : Database = initializeDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            args.getString(PLAYLIST_DATA).let { playlistName ->
                this.playlistName = playlistName!!
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //sets listeners to 2 buttons
        editButton = requireView().findViewById(R.id.editButton)
        editButton.setOnClickListener {
            if(!hasConnectivity(requireContext()) && playlist.savedOnline){
                Toast.makeText(requireContext(),getString(R.string.offline_error_message_toast), Toast.LENGTH_SHORT).show()
                disableButtons()
            }else{
                val intent  = Intent(context, AddPlaylistActivity::class.java)
                intent.putExtra(PLAYLIST_TO_MODIFY, Json.encodeToString(playlist))
                intent.putExtra(ADD_PLAYLIST_FLAG, false)
                startActivity(intent)
            }


        }

        loadPlaylist()

        val playlistTitle : TextView = requireView().findViewById(R.id.PlaylistName)
        playlistTitle.text = playlistName

        deleteButton = requireView().findViewById(R.id.deleteButton)
        deleteButton.setOnClickListener{
            //removes playlist from user playlists

            if(!hasConnectivity(requireContext()) && playlist.savedOnline){
                disableButtons()
                Toast.makeText(requireContext(),getString(R.string.offline_error_message_toast), Toast.LENGTH_SHORT).show()
            }else{
                user.removePlaylist(playlist)
                user.save(requireContext())
                user.onlineCopyAndSave()
                activity?.setResult(0)
                activity?.finish()
            }

        }

        saveOnlineButton = requireView().findViewById(R.id.SaveOnlineButton)
        saveOnlineButton.setOnClickListener {
            if(hasConnectivity(requireContext())){
                playlist.transformToOnline().addOnSuccessListener {
                    playlist.saveOnline()
                }.addOnSuccessListener {
                    user.save(requireContext())
                    user.onlineCopyAndSave()
                    saveOnlineButton.visibility = View.GONE
                }
            }else{
                disableButtons()
                Toast.makeText(requireContext(),getString(R.string.offline_error_message_toast), Toast.LENGTH_SHORT).show()
            }

        }

        importLyricsButton = requireView().findViewById(R.id.ImportLyricsButton)
        importLyricsButton.setOnClickListener {
            if(hasConnectivity(requireContext())){
                importLyricsButton.text = getString(R.string.Loading_lyrics_text)
                loadLyrics(playlist).addOnSuccessListener {
                    user.addPlaylist(playlist)
                    user.save(requireContext())
                    if(playlist.savedOnline){
                        user.onlineCopyAndSave()
                    }
                    importLyricsButton.visibility = View.GONE
                }
            }else{
                disableButtons()
                Toast.makeText(requireContext(),getString(R.string.no_lyrics_generation_offline), Toast.LENGTH_SHORT).show()
            }

        }

    }

    /**
     * this method serve to be able to mock the loadLyrics behavior
     */
    open fun loadLyrics(playlist: Playlist) : Task<Void>{
    return playlist.loadLyrics()
}
    private fun loadPlaylist(){
        User.loadOrDefault(requireContext()).addOnSuccessListener { l ->
            user = l
            playlist = user.getPlaylistWithName(playlistName) ?: Playlist("")
            songs = playlist.getSongs().toList()
            initializeRecyclerView()
            if(playlist.savedOnline){
                saveOnlineButton.visibility = View.GONE
            }
            if(playlist.allSongsHaveLyricsOrHaveTriedFetchingSome()){
                importLyricsButton.visibility = View.GONE
            }
        }.addOnSuccessListener {
            if(!playlist.savedLocally){
               db.getPlaylist(playlist.id).addOnSuccessListener {

                    playlist = it
                    songs = it.getSongs().toList()
                  // playlist.addSongs(songs.toSet())
                    //initializeRecyclerView()
                   val tasks = mutableListOf<Task<Void>>()
                   for(song in songs){
                       tasks.add(db.getSong(song.id).continueWith { s ->
                           song.lyrics = s.result.lyrics
                           null
                       })
                   }
                   Tasks.whenAllComplete(tasks).addOnSuccessListener {
                      // playlist.addSongs(songs.toSet())
                       user.addPlaylist(playlist)
                       user.save(requireContext())
                       initializeRecyclerView()
                   }
                }
            }
        }
    }

    private fun initializeRecyclerView() {
        val songDisplay : RecyclerView = requireView().findViewById(R.id.SongRecyclerView)
        songDisplay.adapter = SongAdapter(songs, this)
        songDisplay.layoutManager = (LinearLayoutManager(activity))
        songDisplay.setHasFixedSize(true)
    }

    private fun disableButtons(){
        importLyricsButton.isEnabled = false
        saveOnlineButton.isEnabled = false
        if(playlist.savedOnline){
            deleteButton.isEnabled = false
            editButton.isEnabled = false
        }
    }
    override fun onItemClick(position: Int) {
        if(!hasConnectivity(requireContext()) && playlist.savedOnline){
            disableButtons()
            Toast.makeText(requireContext(),getString(R.string.offline_error_message_toast), Toast.LENGTH_SHORT).show()
        }else{
            val bundle = Bundle()
            bundle.putString(PLAYLIST_DATA, playlist.name)
            bundle.putStringArray(SONG_DATA, arrayOf(songs[position].name, songs[position].artist))
            activity?.supportFragmentManager?.beginTransaction()
                ?.replace(R.id.container, SongFragment::class.java, bundle)
                ?.addToBackStack("SongFragment")
                ?.commit()
        }

    }
}