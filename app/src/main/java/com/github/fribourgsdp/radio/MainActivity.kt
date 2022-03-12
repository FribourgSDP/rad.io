package com.github.fribourgsdp.radio


import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.fribourgsdp.radio.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

import android.widget.ImageButton

const val USERNAME = "com.github.fribourgsdp.radio.USERNAME"


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val fireBaseButton = findViewById<Button>(R.id.FireBaseButton)
        fireBaseButton.setOnClickListener{
            startActivity(Intent(this,FireBaseTestActivity::class.java))        }

       /* binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonGoToGoogleSignIn.setOnClickListener{
            startActivity(Intent(this, FireBaseTestActivity::class.java))
            finish()
        }*/




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