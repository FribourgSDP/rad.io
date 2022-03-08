package com.github.fribourgsdp.radio

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class DisplayLyricsActivity : AppCompatActivity() {
    private var songTextView : TextView? = null
    private var artistTextView : TextView? = null // TODO: right way to declare textviews ?
    private var resultsTextView : TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_lyrics)
        songTextView = findViewById(R.id.songEditView)
        artistTextView = findViewById(R.id.artistEditView)
        resultsTextView = findViewById(R.id.resultsTextView)
        val goButton : Button = findViewById(R.id.button1)
        goButton.setOnClickListener {
            run {
                val songName: String = songTextView?.text.toString()
                val artistName: String = artistTextView?.text.toString()
                // TODO: handle exception
                val lyricsFuture = LyricsGetter.getLyrics(songName, artistName)
                val lyrics = lyricsFuture.get()
                resultsTextView?.text = lyrics
            }
        }

    }

}