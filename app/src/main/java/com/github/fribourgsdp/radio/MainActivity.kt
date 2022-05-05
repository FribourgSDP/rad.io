package com.github.fribourgsdp.radio



import android.content.Intent
import android.os.Bundle
import android.widget.Button

//import com.github.fribourgsdp.radio.databinding.ActivityMainBinding

import android.widget.ImageButton
import com.github.fribourgsdp.radio.config.MyAppCompatActivity
import com.github.fribourgsdp.radio.config.SettingsActivity
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.data.view.UserProfileActivity
import com.github.fribourgsdp.radio.game.prep.GameSettingsActivity
import com.github.fribourgsdp.radio.game.prep.JoinGameActivity

class MainActivity : MyAppCompatActivity() {
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
        User.createDefaultUser().continueWith{ user ->
            val result = user.result
            result.save(this)
            db.setUser(result.id,result)
        }

    }

    override fun onBackPressed() {
        /* This activity is the main activity of our app and we wish to stay on this*/
    }
}