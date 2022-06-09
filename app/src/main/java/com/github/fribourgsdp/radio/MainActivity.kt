package com.github.fribourgsdp.radio

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.github.fribourgsdp.radio.config.MyAppCompatActivity
import com.github.fribourgsdp.radio.config.SettingsActivity
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.data.view.UserProfileActivity
import com.github.fribourgsdp.radio.database.FirestoreDatabase
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
            checkForConnectivityAndGoTo(Intent(this, GameSettingsActivity::class.java))
        }
        joinButton.setOnClickListener {
            checkForConnectivityAndGoTo(Intent(this, JoinGameActivity::class.java))
        }
        settingsButton.setOnClickListener {startActivity(Intent(this, SettingsActivity::class.java))}
        profileButton.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
            finish()
        }

        if(!hasConnectivity(this)) {
            disableButtonsAndShowToast()
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

    private fun checkForConnectivityAndGoTo( targetActivity : Intent){
        if(hasConnectivity(this)){
            startActivity(targetActivity)
        }else{
            disableButtonsAndShowToast()
        }
    }

    private fun disableButtonsAndShowToast(){
        Toast.makeText(this,getString(R.string.offline_error_message_toast), Toast.LENGTH_SHORT).show()
        disableButtons()

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