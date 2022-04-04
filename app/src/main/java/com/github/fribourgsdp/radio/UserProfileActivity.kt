package com.github.fribourgsdp.radio

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

class UserProfileActivity : AppCompatActivity() {
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

        try {
            user = User.load(this)
        } catch (e: java.io.FileNotFoundException) {
            //this should never happen as a user is created and saved in the first activity
            createDefaultUser()
        }

        instantiateViews()



        checkUser()

        launchSpotifyButton.setOnClickListener {
            authenticateUser()
        }


        saveChangeButton.setOnClickListener{
            updateUser()
        }

        usernameField.setText(user.name)
        usernameInitialText.setText(user.initial.uppercaseChar().toString())
        spotifyStatusText.apply {text = if (user.linkedSpotify) "linked" else "unlinked"}


        homeButton.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        googleSignInButton.setOnClickListener{
            if(signedIn){
                firebaseAuth.signOut()
                googleSignInButton.setText(getString(R.string.sign_in_message))
                signedIn = false

            }else{
                startActivity(Intent(this,GoogleSignInActivity::class.java))
            }
        }

        userIcon.setColorFilter(PorterDuffColorFilter(user.color, PorterDuff.Mode.ADD))

        findViewById<FloatingActionButton>(R.id.addPlaylistButton).setOnClickListener{startActivity(Intent(this, AddPlaylistActivity::class.java))}

        //initialise playlists recycler view fragment
        val bundle = Bundle()
        bundle.putString(USER_DATA, Json.encodeToString(user))
        MyFragment.beginTransaction<UserPlaylistsFragment>(supportFragmentManager, bundle)
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

    //this is theoritecally never called
    private fun createDefaultUser(){
        user = User("Default")
        db.setUser("defUserProfileActivity",user)
        user.save(this)

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
        usernameInitialText.setText(user.initial.uppercaseChar().toString())
    }

    private fun authenticateUser() {
        AuthorizationClient.openLoginInBrowser(this, buildRequest())
    }


    private fun checkUser(){
        //get current user
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null){
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
                googleSignInButton.setText(getString(R.string.sign_out_message))

                signedIn = true
            }
        }
    }
    companion object {
        fun buildRequest(): AuthorizationRequest{
            return AuthorizationRequest.Builder(MY_CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI)
                .setScopes(arrayOf(SCOPES))
                .setShowDialog(true)
                .build()
        }
        fun loadUser(ctx : Context) : User{
            return try { /* TODO replace with proper error handling of asking user enter his info */
                User.load(ctx)
            } catch (e: java.io.FileNotFoundException) {
                User("No User Found", User.generateColor())
            }
        }
    }
}