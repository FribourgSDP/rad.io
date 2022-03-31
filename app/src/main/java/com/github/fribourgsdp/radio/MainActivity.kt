package com.github.fribourgsdp.radio



import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val voiceOverIpButton = findViewById<Button>(R.id.VoiceOverIpButton)
        voiceOverIpButton.setOnClickListener{startActivity(Intent(this,VoiceOverIPActivity::class.java))}
        val playButton = findViewById<Button>(R.id.playButton)
        playButton.setOnClickListener {startActivity(Intent(this, GameSettingsActivity::class.java))}
        val joinButton : Button = findViewById(R.id.joinButton)
        joinButton.setOnClickListener {startActivity(Intent(this, JoinGameActivity::class.java))}
        val settingsButton = findViewById<Button>(R.id.settingsButton)
        settingsButton.setOnClickListener {startActivity(Intent(this, SettingsActivity::class.java))}
        val button : Button = findViewById(R.id.button)
        button.setOnClickListener {startActivity(Intent(this, DisplayLyricsActivity::class.java))}
        val profileButton: ImageButton = findViewById(R.id.profileButton)
        profileButton.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }

    }
}