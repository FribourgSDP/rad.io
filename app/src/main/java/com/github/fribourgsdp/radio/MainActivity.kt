package com.github.fribourgsdp.radio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton

const val USERNAME = "com.github.fribourgsdp.radio.USERNAME"

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
        button.setOnClickListener {
            startActivity(Intent(this, DisplayLyricsActivity::class.java))
        }

        val profileButton: ImageButton = findViewById(R.id.profileButton)
        profileButton.setOnClickListener {
            val intent : Intent = Intent(this, UserProfileActivity::class.java).apply {
                putExtra(USERNAME, "Default")
            }
            startActivity(intent)
        }
    }
}