package com.github.fribourgsdp.radio

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.tasks.Tasks
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val MY_CLIENT_ID = "9dc40237547f4ffaa41bf1e07ea0bba1"
const val REDIRECT_URI = "com.github.fribourgsdp.radio://callback"
const val SCOPES = "playlist-read-private,playlist-read-collaborative"
const val RECREATE_USER = "com.github.fribourgsdp.radio.avoidRecreatingUser"
const val USER_DATA = "com.github.fribourgsdp.radio.USER_DATA"

open class UserProfileActivity : MyAppCompatActivity() {
    private lateinit var user : User
    private lateinit var usernameField : EditText
    private lateinit var usernameInitialText : TextView
    private lateinit var spotifyStatusText : TextView
    private lateinit var saveChangeButton : Button
    private lateinit var launchSpotifyButton : Button
    private lateinit var homeButton : Button
    private lateinit var googleSignInButton : Button
    private lateinit var userIcon : ImageView
    private var signedIn : Boolean = false

    private var db = FirestoreDatabase()


    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        instantiateViews()

        User.loadOrDefault(this).addOnSuccessListener { u ->
            user = u

            checkUser()

            usernameField.setText(user.name)
            usernameInitialText.text = user.initial.uppercaseChar().toString()
            spotifyStatusText.apply { text = if (user.linkedSpotify) getString(R.string.spotify_linked) else getString(R.string.spotify_unlinked) }
            userIcon.colorFilter = PorterDuffColorFilter(user.color, PorterDuff.Mode.ADD)
            //initialise playlists recycler view fragment
            val bundle = Bundle()
            bundle.putString(USER_DATA, Json.encodeToString(user))
            MyFragment.beginTransaction<UserPlaylistsFragment>(supportFragmentManager, bundle)
        }

        launchSpotifyButton.setOnClickListener {
            authenticateUser()
        }



        saveChangeButton.setOnClickListener {
            updateUser()
        }

        homeButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }


        googleSignInButton.setOnClickListener {
            if (signedIn) {
                firebaseAuth.signOut()
                googleSignInButton.setText(getString(R.string.sign_in_message))
                signedIn = false

            } else {
                startActivity(Intent(this, GoogleSignInActivity::class.java))
            }



        }
        findViewById<FloatingActionButton>(R.id.addPlaylistButton).setOnClickListener{startActivity(Intent(this, AddPlaylistActivity::class.java))}

    }

    private fun instantiateViews(){
        firebaseAuth = FirebaseAuth.getInstance()
        usernameField = findViewById(R.id.username)
        launchSpotifyButton = findViewById(R.id.launchSpotifyButton)
        homeButton = findViewById(R.id.homeButton)
        saveChangeButton = findViewById(R.id.saveUserButton)
        usernameInitialText = findViewById(R.id.usernameInitial)
        spotifyStatusText = findViewById(R.id.spotifyStatus)
        googleSignInButton = findViewById(R.id.googleSignInButton)
        userIcon = findViewById(R.id.userIcon)
    }


    private fun updateUser(){
        user.name = usernameField.text.toString()
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser == null){
            db.setUser(user.id,user)
        }else{
            db.setUser(firebaseUser.uid,user)
        }

        user.save(this)
        usernameInitialText.text = user.initial.uppercaseChar().toString()
    }

    /**
     * This does the authetication to spotify
     */
    private fun authenticateUser() {
        AuthorizationClient.openLoginInBrowser(this, buildRequest())
    }


    open fun checkUser(){
        //get current user
        val firebaseUser = firebaseAuth.currentUser

        if(firebaseUser != null){
            //check whether it is a new user of not, if yes, we saved the default user info in the cloud
            //if no we load the data from the cloud.
            val mockUser = db.getUser(firebaseUser.uid)
            mockUser.addOnSuccessListener { l ->

                if(l == null){
                    db.setUser(firebaseUser.uid,user)
                }else if(l.id != user.id){
                    user = l
                    user.save(this)
                }
                usernameField.setText(user.name)
                googleSignInButton.text = getString(R.string.sign_out_message)

                signedIn = true
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(RECREATE_USER, false)
        startActivity(intent)
        finish()
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