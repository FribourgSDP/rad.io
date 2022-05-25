package com.github.fribourgsdp.radio.data.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.fribourgsdp.radio.*
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.database.DatabaseHolder
import com.github.fribourgsdp.radio.external.musixmatch.LyricsGetter
import com.github.fribourgsdp.radio.external.musixmatch.MusixmatchLyricsGetter
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

open class PlaylistSongsFragment : MyFragment(R.layout.fragment_playlist_display), OnClickListener,
    DatabaseHolder {
    private lateinit var playlist: Playlist
    private lateinit var songs: List<Song>
    private lateinit var playlistName: String
    private lateinit var editButton: Button
    private lateinit var deleteButton: Button
    private lateinit var saveOnlineButton : Button
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
            val intent  = Intent(context, AddPlaylistActivity::class.java)
            intent.putExtra(PLAYLIST_TO_MODIFY, Json.encodeToString(playlist))
            startActivity(intent)
        }

        loadPlaylist()

        val playlistTitle : TextView = requireView().findViewById(R.id.PlaylistName)
        playlistTitle.text = playlistName

        deleteButton = requireView().findViewById(R.id.deleteButton)
        deleteButton.setOnClickListener{
            //removes playlist from user playlists
            user.removePlaylist(playlist)
            user.save(requireContext())
            user.onlineCopyAndSave()
            activity?.setResult(0)
            activity?.finish()
        }

        saveOnlineButton = requireView().findViewById(R.id.SaveOnlineButton)
        saveOnlineButton.setOnClickListener {
            playlist.transformToOnline().addOnSuccessListener {
                playlist.saveOnline()
            }.addOnSuccessListener {
                user.save(requireContext())
                user.onlineCopyAndSave()
                saveOnlineButton.visibility = View.INVISIBLE
            }
        }

        importLyricsButton = requireView().findViewById(R.id.ImportLyricsButton)
        importLyricsButton.setOnClickListener {
            //loadLyrics(playlist).addOnSuccessListener {
              playlist.loadLyrics().addOnSuccessListener {
                //playlist = it
                user.addPlaylist(playlist)
                user.save(requireContext())
                if(playlist.savedOnline){
                   user.onlineCopyAndSave()
                }
                importLyricsButton.visibility = View.INVISIBLE
                Log.i("oui",playlist.getSongs().toList()[0].lyrics.toString())
                //TODO("save this playlist a bit more that just this")

            }
        }




    }


    private fun loadPlaylist(){
        User.loadOrDefault(requireContext()).addOnSuccessListener { l ->
            user = l
            playlist = user.getPlaylistWithName(playlistName) ?: Playlist("")
            songs = playlist.getSongs().toList()
            initializeRecyclerView()
            if(playlist.savedOnline){
                saveOnlineButton.visibility = View.INVISIBLE
            }
            if(playlist.allSongsHaveLyrics()){
                importLyricsButton.visibility = View.INVISIBLE
            }
        }.addOnSuccessListener {
            if(!playlist.savedLocally){
               db.getPlaylist(playlist.id).addOnSuccessListener {
                    playlist = it
                    songs = playlist.getSongs().toList()
                    initializeRecyclerView()
                }
            }
        }
    }

    /*protected fun loadLyrics(playlist : Playlist, lyricsGetter: LyricsGetter = MusixmatchLyricsGetter) : Task<Playlist> {
        //this one should only import lyrics for song that have no lyrics
        val playlistWithLyrics = Playlist(playlist.name,playlist.genre)
        val tasks = mutableListOf<Task<Void>>()
        for (song in playlist.getSongs()){
            if(song.lyrics == "") {
                lyricsGetter.getLyrics(song.name,song.artist)
                tasks.add(
                    Tasks.forResult(lyricsGetter.getLyrics(song.name, song.artist).thenAccept { f ->
                    val songWithLyrics = Song(song.name, song.artist, f)
                    playlistWithLyrics.addSong(songWithLyrics)
                }.join()
                ))
            }
        }
        return Tasks.whenAllComplete(tasks).continueWith {
            playlistWithLyrics
        }
    }*/

    private fun initializeRecyclerView() {
        val songDisplay : RecyclerView = requireView().findViewById(R.id.SongRecyclerView)
        songDisplay.adapter = SongAdapter(songs, this)
        songDisplay.layoutManager = (LinearLayoutManager(activity))
        songDisplay.setHasFixedSize(true)
    }

    override fun onItemClick(position: Int) {
        val bundle = Bundle()
        bundle.putString(PLAYLIST_DATA, playlist.name)
        bundle.putStringArray(SONG_DATA, arrayOf(songs[position].name, songs[position].artist))
        activity?.supportFragmentManager?.beginTransaction()
            ?.replace(R.id.container, SongFragment::class.java, bundle)
            ?.addToBackStack("SongFragment")
            ?.commit()
    }
}