package com.github.fribourgsdp.radio

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class SongFragment : MyFragment(R.layout.fragment_song) {
    private lateinit var initialLyrics : String
    private lateinit var currentLyrics : String
    private lateinit var playlist : Playlist
    private lateinit var song: Song

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            args.getString(PLAYLIST_DATA).let { serializedPlaylist ->
                playlist = Json.decodeFromString(serializedPlaylist!!)
            }
            args.getString(SONG_DATA).let { serializedSong ->
                println("**************** DECODING JSON DATA FOR SONG")
                song = Json.decodeFromString(serializedSong!!)
                initialLyrics =
                    if(song.lyrics == ""){
                        println("CALLING LYRICSGETTER")
                        try {
                            LyricsGetter.getLyrics(song.name, song.artist)
                                .get(2000, TimeUnit.MILLISECONDS)
                        } catch (e : TimeoutException){
                            ""
                        }
                    } else{
                        song.lyrics
                    }
                currentLyrics = initialLyrics
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val songTitle : TextView = requireView().findViewById(R.id.SongName)
        val artistTitle : TextView = requireView().findViewById(R.id.ArtistName)
        val lyricsEditText : EditText = requireView().findViewById(R.id.editTextLyrics)

        songTitle.text = song.name
        artistTitle.text = song.artist
        lyricsEditText.setText(initialLyrics)

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

        if (currentLyrics != initialLyrics) {
            val user = User.load(requireView().context)
            song.lyrics = currentLyrics
            playlist.addSong(song)
            user.addPlaylist(playlist)
            user.save(requireView().context)
        }
    }
}