package com.github.fribourgsdp.radio

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class SongFragment : MyFragment(R.layout.fragment_song) {
    private lateinit var initialLyrics : String
    private lateinit var currentLyrics : String
    private lateinit var playlistName : String
    private lateinit var songName: String
    private lateinit var playlist: Playlist
    private lateinit var song: Song

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            args.getString(PLAYLIST_DATA).let { playlistName ->
                this.playlistName = playlistName!!
            }
            args.getString(SONG_DATA).let { songName ->
                this.songName = songName!!
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val songTitle : TextView = requireView().findViewById(R.id.SongName)
        val artistTitle : TextView = requireView().findViewById(R.id.ArtistName)
        val lyrics : EditText = requireView().findViewById(R.id.editTextLyrics)

        val user = User.load(requireContext())
        playlist = user.getPlaylistWithName(playlistName)
        song = playlist.getSong(songName)
        initialLyrics = song.lyrics
        currentLyrics = initialLyrics

        songTitle.text = song.name
        artistTitle.text = song.artist
        lyrics.setText(song.lyrics)

        lyrics.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                currentLyrics = lyrics.text.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()

        if (currentLyrics != initialLyrics) {
            val user = User.load(requireContext())
            user.removePlaylist(playlist)
            playlist.removeSong(song)
            song.lyrics = currentLyrics
            playlist.addSong(song)
            user.addPlaylist(playlist)
            user.save(requireView().context)
        }
    }
}