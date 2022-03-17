package com.github.fribourgsdp.radio

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import okhttp3.OkHttpClient


class DisplayLyricsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_lyrics)
        val songTextView : TextView = findViewById(R.id.songEditView)
        val artistTextView : TextView = findViewById(R.id.artistEditView)
        val resultsTextView : TextView = findViewById(R.id.resultsTextView)
        val goButton : Button = findViewById(R.id.button1)
        goButton.setOnClickListener {
            run {
                val songName: String = songTextView.text.toString()
                val artistName: String = artistTextView.text.toString()
                val lyricsFuture = LyricsGetter.getLyrics(songName, artistName, OkHttpClient())
                val lyrics = lyricsFuture.get()
                resultsTextView.text = HtmlCompat.fromHtml(LyricsGetter.markSongName(lyrics, songName), HtmlCompat.FROM_HTML_MODE_LEGACY)
            }
        }

    }

}