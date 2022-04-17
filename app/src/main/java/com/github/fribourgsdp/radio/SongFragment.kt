package com.github.fribourgsdp.radio

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.github.fribourgsdp.radio.mockimplementations.MockLyricsGetter
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.lang.ClassCastException

const val USE_MOCK_LYRICS_GETTER = "com.github.fribourgsdp.radio.USE_MOCK_LYRICS_GETTER"

class SongFragment : MyFragment(R.layout.fragment_song) {
    private lateinit var initialLyrics : String
    private lateinit var currentLyrics : String
    private lateinit var playlist : Playlist
    private lateinit var song: Song
    private var doSaveLyrics : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            args.getString(PLAYLIST_DATA).let { serializedPlaylist ->
                playlist = Json.decodeFromString(serializedPlaylist!!)
            }
            //if the value USE_MOCK_LYRICS_GETTER is 1, then it will not call Musixmatch.
            val lyricsGetter = if (args.getInt(USE_MOCK_LYRICS_GETTER) == 1){
                MockLyricsGetter
            } else{
                MusixmatchLyricsGetter
            }
            args.getString(SONG_DATA).let { serializedSong ->

                song = Json.decodeFromString(serializedSong!!)
                initialLyrics = song.lyrics
                currentLyrics = initialLyrics
                if(song.lyrics == ""){
                    fetchLyrics(lyricsGetter)
                }
            }
        }
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

            val parentFragment = parentFragmentManager.fragments[0]
            if (parentFragment is PlaylistSongsFragment){
                //trigger reloading of playlist
                parentFragment.loadPlaylist()
            }
        }
    }
}