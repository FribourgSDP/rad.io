package com.github.fribourgsdp.radio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

const val GAME_UID_KEY = "com.github.fribourgsdp.radio.GAME_UID"


class JoinGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_game)

        val joinButton : Button = findViewById(R.id.joinButton)

    }
}