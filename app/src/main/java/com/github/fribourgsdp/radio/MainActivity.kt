package com.github.fribourgsdp.radio

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button : Button = findViewById(R.id.button)
        button.setOnClickListener { v ->
            val i = Intent(this@MainActivity, DisplayLyricsActivity::class.java)
            startActivity(i)
        }
    }
}