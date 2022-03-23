package com.github.fribourgsdp.radio

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.google.firebase.auth.FirebaseAuth

const val MY_CLIENT_ID = "9dc40237547f4ffaa41bf1e07ea0bba1"
const val REDIRECT_URI = "com.github.fribourgsdp.radio://callback"
const val SCOPES = "playlist-read-private,playlist-read-collaborative"
const val COMING_FROM_SPOTIFY_ACTIVITY_FLAG = "com.github.fribourgsdp.radio.spotifyDataParsing"

class UserProfileActivity : AppCompatActivity() {
    private lateinit var user : User

    //firebase auth
    lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        val playButton = findViewById<Button>(R.id.launchSpotifyButton)
        playButton.setOnClickListener {
            authenticateUser()
        }

        /* TODO REPLACE WITH PROPER FETCH OF USER*/
        user = User("Default")

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
        if (intent.getBooleanExtra(COMING_FROM_SPOTIFY_ACTIVITY_FLAG, false)){
            addPlaylistsToUser()
        }
    }


    private fun addPlaylistsToUser(){
        val map = intent!!.getSerializableExtra("nameToUid") as HashMap<String, String>
        user.addSpotifyPlaylistUids(map)
        val playlists = intent!!.getSerializableExtra("playlists") as HashSet<Playlist>
        user.addPlaylists(playlists)
    }


    private fun authenticateUser() {
        AuthorizationClient.openLoginInBrowser(this, buildRequest())
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
    companion object {
        fun buildRequest(): AuthorizationRequest{
            return AuthorizationRequest.Builder(MY_CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI)
                .setScopes(arrayOf(SCOPES))
                .setShowDialog(true)
                .build()
        }
    }
}