package com.github.fribourgsdp.radio

import android.content.Intent
import android.os.Bundle
import android.util.Log

class TransitionQuitGameActivity: MyAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("Transition", "in transition")
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}