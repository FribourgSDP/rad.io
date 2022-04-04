package com.github.fribourgsdp.radio



import android.content.ContentValues
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

//import com.github.fribourgsdp.radio.databinding.ActivityMainBinding

import android.widget.ImageButton

class MainActivity : AppCompatActivity() {
    private val db = FirestoreDatabase()
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

        /** this user allows quick demo's as it is data that is written to the app
         * specific storage and can be easily read without intents */
        try {
            //if this is successful, we don't need to do anything
            val user = User.load(this)

        } catch (e: java.io.FileNotFoundException) {
            createUser()
        }


    }
    private fun createUser(){

        //for now, we set to 1 the user ID, we will then use the database function
         db.generateUserId().addOnSuccessListener { id->
            val userName = "Guest"
            val generatedUser = User(userName)
            generatedUser.id = id.toString()
            generatedUser.save(this)
            db.setUser(id.toString(),generatedUser)
        }

    }
}