package com.github.fribourgsdp.radio

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class UserProfileActivity : AppCompatActivity() {
    private lateinit var user : User

    //firebase auth
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        /* TODO REPLACE WITH PROPER FETCH OF USER*/
        user = User(intent.getStringExtra(USERNAME)!!)

        findViewById<TextView>(R.id.username).apply {
            text = user.name
        }

        findViewById<TextView>(R.id.usernameInitial).apply {
            text = user.initial.toString()
        }

        findViewById<TextView>(R.id.spotifyStatus).apply {
            text = if (user.spotifyLinked) "linked" else "unlinked"
        }




        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        val logoutButton: Button = findViewById(R.id.logoutButton)
        logoutButton.setOnClickListener{
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }




        findViewById<ImageView>(R.id.userIcon).setColorFilter(PorterDuffColorFilter(user.color, PorterDuff.Mode.ADD))
    }



    private fun checkUser(){
        //get current user
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null){
            //user not logged in
            startActivity(Intent(this, GoogleSignInActivity::class.java))
            finish()
        }else{
            //user logged in
            //get user info
            /* TODO MAP FIREBASE INFO TO USER*/

        }
    }

}