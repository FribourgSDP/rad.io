package com.github.fribourgsdp.radio

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
    private lateinit var usernameField : EditText
    private lateinit var usernameInitialText : TextView
    private lateinit var spotifyStatusText : TextView
    private lateinit var saveChangeButton : Button
    private lateinit var launchSpotifyButton : Button
    private lateinit var logoutButton : Button
    private lateinit var googleSignInButton : Button
    private lateinit var playlistDisplay : RecyclerView
    private lateinit var userIcon : ImageView

    private var db = FirestoreDatabase()

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)

        instantiateViews()


        try { /* TODO replace with proper error handling of asking user enter his info */
             user = User.load(this)
        } catch (e: java.io.FileNotFoundException) {
            //this should never happen
            createDefaultUser()
        }

        checkUser()

        launchSpotifyButton.setOnClickListener {
            authenticateUser()
        }

        saveChangeButton.setOnClickListener{
            updateUser()
        }
        usernameField.setText(user.name)
        usernameInitialText.setText(user.initial.uppercaseChar().toString())
        spotifyStatusText.apply {text = if (user.spotifyLinked) "linked" else "unlinked"}


        logoutButton.setOnClickListener{
            firebaseAuth.signOut()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        googleSignInButton.setOnClickListener{
            startActivity(Intent(this,GoogleSignInActivity::class.java))
        }


        userIcon.setColorFilter(PorterDuffColorFilter(user.color, PorterDuff.Mode.ADD))
        if (intent.getBooleanExtra(COMING_FROM_SPOTIFY_ACTIVITY_FLAG, false)){
            addPlaylistsToUser()
        }

        userPlaylists = user.getPlaylists().toList()
        playlistDisplay.adapter = PlaylistAdapter(userPlaylists, this)
        playlistDisplay.layoutManager = (LinearLayoutManager(this))
        playlistDisplay.setHasFixedSize(true)
    }

    override fun onItemClick(position: Int) {
        val intent = Intent(this, PlaylistDisplayActivity::class.java)
            .putExtra(PLAYLIST_DATA, Json.encodeToString(userPlaylists[position]))
        startActivity(intent)
    }

    private fun instantiateViews(){
        firebaseAuth = FirebaseAuth.getInstance()
        usernameField = findViewById(R.id.username)
        launchSpotifyButton = findViewById(R.id.launchSpotifyButton)
        logoutButton = findViewById(R.id.logoutButton)
        saveChangeButton = findViewById(R.id.saveUserButton)
        usernameInitialText = findViewById(R.id.usernameInitial)
        spotifyStatusText = findViewById(R.id.spotifyStatus)
        googleSignInButton = findViewById(R.id.googleSignInButton)
        playlistDisplay = findViewById(R.id.playlist_recycler_view)
        userIcon = findViewById(R.id.userIcon)
    }
    //I think this should be on the main page, this way, we don't have to go to this activity to lauch a game
    private fun createDefaultUser(){
        user = User("Default")
        db.setUser("1",user)
        user.save(this)

    }

    private fun updateUser(){
        user = User(usernameField.text.toString())
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null){
            db.setUser("1",user)
        }else{
            db.setUser(firebaseUser.uid,user)
        }
        user.save(this)
    }

    private fun prefillFields(){



    };
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
            //startActivity(Intent(this, GoogleSignInActivity::class.java))
            //finish()
        }else{
            //check whether it is a new user of not, if yes, we saved the default user info in the cloud
            //if no we load the data from the cloud.
            var mockUser = db.getUser(firebaseUser.uid)
            mockUser.addOnSuccessListener { l ->
                if(l == null){
                    db.setUser(firebaseUser.uid,user)
                }else{
                    user = l
                    user.save(this)
                }
                usernameField.setText(user.name)

            }

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