package com.github.fribourgsdp.radio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val playButton = findViewById<Button>(R.id.playButton)
        playButton.setOnClickListener {
            startActivity(Intent(this, GameSettingsActivity::class.java))
        }

        val settingsButton = findViewById<Button>(R.id.settingsButton)
        settingsButton.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
        
        val button : Button = findViewById(R.id.button)
        button.setOnClickListener { v ->
            val i = Intent(this, DisplayLyricsActivity::class.java)
            startActivity(i)
        }

        val profileButton: ImageButton = findViewById(R.id.profileButton)
        profileButton.setOnClickListener {
            startActivity(Intent(this, UserProfile::class.java))
        }
    }
}