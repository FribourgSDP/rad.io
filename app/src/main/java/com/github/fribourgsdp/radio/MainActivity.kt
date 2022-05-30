package com.github.fribourgsdp.radio



import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button

//import com.github.fribourgsdp.radio.databinding.ActivityMainBinding

import android.widget.ImageButton
import android.widget.Toast
import com.github.fribourgsdp.radio.config.MyAppCompatActivity
import com.github.fribourgsdp.radio.config.SettingsActivity
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.data.view.UserProfileActivity
import com.github.fribourgsdp.radio.database.FirestoreDatabase
import com.github.fribourgsdp.radio.deprecated.VoiceOverIPActivity
import com.github.fribourgsdp.radio.game.prep.GameSettingsActivity
import com.github.fribourgsdp.radio.game.prep.JoinGameActivity

open class MainActivity : MyAppCompatActivity() {
    private val db = FirestoreDatabase()
    private lateinit var playButton : Button
    private lateinit var joinButton : Button
    private lateinit var settingsButton : Button
    private lateinit var profileButton : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        playButton.setOnClickListener {
            if(hasConnectivity(this)){
                startActivity(Intent(this, GameSettingsActivity::class.java))
            }
            else{
                Toast.makeText(this,getString(R.string.offline_error_message_toast), Toast.LENGTH_SHORT).show()
                disableButtons()
            }
        }
        joinButton.setOnClickListener {
            if(hasConnectivity(this)){
                startActivity(Intent(this, JoinGameActivity::class.java))
            }else{
                Toast.makeText(this,getString(R.string.offline_error_message_toast), Toast.LENGTH_SHORT).show()
                disableButtons()
            }
        }
        settingsButton.setOnClickListener {startActivity(Intent(this, SettingsActivity::class.java))}
        profileButton.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }

        if(!hasConnectivity(this)) {
            disableButtons()
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

    private fun disableButtons(){
        playButton.isEnabled = false
        joinButton.isEnabled = false


    }
    private fun initViews(){
         settingsButton = findViewById(R.id.settingsButton)
         joinButton  = findViewById(R.id.joinButton)
         playButton = findViewById(R.id.playButton)
         profileButton = findViewById(R.id.profileButton)


    }
    private fun createUser(){
        User.createDefaultUser(this).continueWith{ user ->
            val result = user.result
            result.save(this)
            db.setUser(result.id,result)
        }

    }

    override fun onBackPressed() {
        /* This activity is the main activity of our app and we wish to stay on this*/
    }
}