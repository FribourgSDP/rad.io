package com.github.fribourgsdp.radio.data.view

import android.content.ContentValues
import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.github.fribourgsdp.radio.*
import com.github.fribourgsdp.radio.config.MyAppCompatActivity
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.database.DatabaseHolder
import com.github.fribourgsdp.radio.database.FirestoreDatabase
import com.github.fribourgsdp.radio.external.google.GoogleSignInActivity
import com.github.fribourgsdp.radio.util.MyFragment
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
const val USER_DATA = "com.github.fribourgsdp.radio.data.view.USER_DATA"

open class UserProfileActivity : MyAppCompatActivity(), KeepOrDismissPlaylistDialog.OnPickListener, MergeDismissImportPlaylistDialog.OnPickListener, DatabaseHolder {
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

    private var db = initializeDatabase()


    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        instantiateViews()

        val fromGoogle = intent.getBooleanExtra("FromGoogle",false)


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
            if(fromGoogle/*comeFromGoogleSignIn*/){
                //check if the connection was sucessful, if yes, do the afterSignInProcedure
                afterSignInProcedure()
            }
        }

        launchSpotifyButton.setOnClickListener {
            authenticateUser()
        }

        saveChangeButton.setOnClickListener {
            //this behavior should change or the this button should diseapper
            updateUser()
        }

        //this button should disappear
        homeButton.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }


        googleSignInButton.setOnClickListener {
            if (signedIn) {
                signOut()
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
        //at this point, the userId should be the firebaseUser.uid
        db.setUser(user.id,user)
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

    override fun onPick(choice: MergeDismissImportPlaylistDialog.Choice) {
        when (choice){
            MergeDismissImportPlaylistDialog.Choice.MERGE -> mergePlaylist()
            MergeDismissImportPlaylistDialog.Choice.IMPORT -> importPlaylist()
            MergeDismissImportPlaylistDialog.Choice.DISMISS_ONLINE -> dismissOnlinePlaylist()
        }.addOnSuccessListener {
            user.save(this)
        }

    }

    private fun mergePlaylist() : Task<Unit>{
        Log.w(ContentValues.TAG, "MERGE", )
        return db.getUser(user.id).continueWith {
            user.addPlaylists(it.result.getPlaylists())
            user.isGoogleUser = true
            user.name = it.result.name
            user.id = it.result.id
            db.setUser(user.id,user)
        }
    }
    private fun importPlaylist() : Task<Unit>{
        Log.w(ContentValues.TAG, "IMPORT", )
        user.removePlaylists(user.getPlaylists())
        return db.getUser(user.id).continueWith{
            user = it.result
            user.name = it.result.name
            user.id = it.result.id
            user.isGoogleUser = true
        }
    }

    private fun dismissOnlinePlaylist() : Task<Unit>{
        Log.w(ContentValues.TAG, "DISMISS", )
        return db.getUser(user.id).continueWith{
            user.name = it.result.name
            user.id = it.result.id
            user.isGoogleUser = true
        }
    }

    override fun onPick(choice: KeepOrDismissPlaylistDialog.Choice) {
        when (choice){
            KeepOrDismissPlaylistDialog.Choice.KEEP -> keepPlaylist()
            KeepOrDismissPlaylistDialog.Choice.DISMISS -> deletePlaylist()
        }.addOnSuccessListener{
            User.createDefaultUser().continueWith {
                user.name = it.result.name
                user.id = it.result.id
                user.isGoogleUser = false
                user.save(this)
            }
        }

    }

    private fun keepPlaylist() : Task<Unit>{
        Log.w(ContentValues.TAG, "KEEP", )
        return Tasks.forResult(null)
        //when keeping playlist, decide whether only those saved locally should be kept or all of them

    }
    private fun deletePlaylist() : Task<Unit> {
        user.removePlaylists(user.getPlaylists())
        Log.w(ContentValues.TAG, "DELETE", )
        return Tasks.forResult(null)

    }





    //this should only happen on successful signIn,
    private fun afterSignInProcedure(){
        user.id = firebaseAuth.currentUser?.uid ?: user.id //MAYBE THROW AN ERROR INSTEAD
        user.isGoogleUser = true
        user.save(this)
        val mergeDismissImportPlaylistPicker = MergeDismissImportPlaylistDialog(this)
        mergeDismissImportPlaylistPicker.show(supportFragmentManager, "mergeDismissImportPlaylistPicker")


    }

    private fun signOut(){
        val keepDismissDialogPicker = KeepOrDismissPlaylistDialog(this)
        keepDismissDialogPicker.show(supportFragmentManager, "keepDismissDialogPicker")

        firebaseAuth.signOut()
        googleSignInButton.setText(getString(R.string.sign_in_message))
        signedIn = false

    }


}