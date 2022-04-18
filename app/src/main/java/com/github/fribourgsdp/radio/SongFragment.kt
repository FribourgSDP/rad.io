package com.github.fribourgsdp.radio

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView

open class SongFragment : MyFragment(R.layout.fragment_song) {
    private lateinit var initialLyrics : String
    private lateinit var currentLyrics : String
    private lateinit var playlistName : String
    private lateinit var songName: String
    private lateinit var playlist: Playlist
    private lateinit var song: Song
    private var doSaveLyrics : Boolean = false

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

    protected open fun getLyricsGetter(): LyricsGetter {
        return MusixmatchLyricsGetter
    }

    private fun fetchLyrics(lyricsGetter: LyricsGetter) {
        lyricsGetter.getLyrics(song.name, song.artist)
            .exceptionally { "" }
            .thenAccept{f ->
                currentLyrics = f
                doSaveLyrics = true
                updateLyrics(requireView().findViewById(R.id.editTextLyrics))
            }
    }

    private fun updateLyrics(lyricsEditText : EditText){
        lyricsEditText.setText(currentLyrics)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val songTitle : TextView = requireView().findViewById(R.id.SongName)
        val artistTitle : TextView = requireView().findViewById(R.id.ArtistName)
        val lyricsEditText : EditText = requireView().findViewById(R.id.editTextLyrics)

        val user = User.load(requireContext())
        playlist = user.getPlaylistWithName(playlistName)
        song = playlist.getSong(songName)
        initialLyrics = song.lyrics
        currentLyrics = initialLyrics
        if(song.lyrics == ""){
            fetchLyrics(getLyricsGetter())
        }

        songTitle.text = song.name
        artistTitle.text = song.artist
        updateLyrics(lyricsEditText)

        lyricsEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                currentLyrics = lyricsEditText.text.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if ((currentLyrics != initialLyrics )|| (doSaveLyrics)) {
            val user = User.load(requireView().context)
            song.lyrics = currentLyrics
            user.updateSongInPlaylist(playlist, song)
            user.save(requireView().context)
        }
    }
}