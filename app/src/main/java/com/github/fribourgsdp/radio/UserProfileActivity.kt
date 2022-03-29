package com.github.fribourgsdp.radio

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val MY_CLIENT_ID = "9dc40237547f4ffaa41bf1e07ea0bba1"
const val REDIRECT_URI = "com.github.fribourgsdp.radio://callback"
const val SCOPES = "playlist-read-private,playlist-read-collaborative"
const val PLAYLIST_DATA = "com.github.fribourgsdp.radio.PLAYLIST_INNER_DATA"
const val COMING_FROM_SPOTIFY_ACTIVITY_FLAG = "com.github.fribourgsdp.radio.spotifyDataParsing"

class UserProfileActivity : AppCompatActivity(), PlaylistAdapter.OnPlaylistClickListener {
    private lateinit var user : User
    private lateinit var userPlaylists : List<Playlist>

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        user = try { /* TODO replace with proper error handling of asking user enter his info */
            User.load(this)
        } catch (e: java.io.FileNotFoundException) {
            User("No User Found", User.generateColor())
        }

        val playButton = findViewById<Button>(R.id.launchSpotifyButton)
        playButton.setOnClickListener {
            authenticateUser()
        }

        findViewById<TextView>(R.id.username).apply {
            text = user.name
        }

        findViewById<TextView>(R.id.usernameInitial).apply {
            text = user.initial.uppercaseChar().toString()
        }

        findViewById<TextView>(R.id.spotifyStatus).apply {
            text = if (user.spotifyLinked) "linked" else "unlinked"
        }


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
        if (intent.getBooleanExtra(COMING_FROM_ADD_PLAYLIST_ACTIVITY_FLAG, false)){
            addManualPlaylistToUser()
        }

        userPlaylists = user.getPlaylists().toList()
        val playlistDisplay : RecyclerView = findViewById(R.id.playlist_recycler_view)
        playlistDisplay.adapter = PlaylistAdapter(userPlaylists, this)
        playlistDisplay.layoutManager = (LinearLayoutManager(this))
        playlistDisplay.setHasFixedSize(true)

        findViewById<FloatingActionButton>(R.id.addPlaylistButton).setOnClickListener{startActivity(Intent(this, AddPlaylistActivity::class.java))}
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, PlaylistDisplayActivity::class.java)
            .putExtra(PLAYLIST_DATA, Json.encodeToString(userPlaylists[position]))
        startActivity(intent)
    }


    private fun addPlaylistsToUser(){
        val map = intent!!.getSerializableExtra("nameToUid") as HashMap<String, String>
        user.addSpotifyPlaylistUids(map)
        val playlists = intent!!.getSerializableExtra("playlists") as HashSet<Playlist>
        user.addPlaylists(playlists)
    }

    private fun addManualPlaylistToUser(){
        val playlist = intent!!.getSerializableExtra("playlists") as HashSet<Playlist>
        user.addPlaylists(playlist)
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