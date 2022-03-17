package com.github.fribourgsdp.radio

import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView

class UserProfileActivity : AppCompatActivity() {
    private lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        /* TODO REPLACE WITH PROPER FETCH OF USER*/
        user = User(intent.getStringExtra(USERNAME)!!, User.generateColor())

        findViewById<TextView>(R.id.username).apply {
            text = user.name
        }

        findViewById<TextView>(R.id.usernameInitial).apply {
            text = user.initial.toString()
        }

        findViewById<TextView>(R.id.spotifyStatus).apply {
            text = if (user.spotifyLinked) "linked" else "unlinked"
        }

        findViewById<ImageView>(R.id.userIcon).setColorFilter(PorterDuffColorFilter(user.color, PorterDuff.Mode.ADD))
    }
}