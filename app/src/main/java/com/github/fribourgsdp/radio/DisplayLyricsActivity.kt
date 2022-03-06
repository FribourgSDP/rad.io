package com.github.fribourgsdp.radio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class DisplayLyricsActivity : AppCompatActivity() {
    private var songTextView : TextView? = null
    private var artistTextView : TextView? = null // TODO: right way to declare textviews ?
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_lyrics)
        songTextView = findViewById(R.id.songEditView)
        artistTextView = findViewById(R.id.artistEditView)
        
    }

}