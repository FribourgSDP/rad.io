package com.github.fribourgsdp.radio.data.view

import android.content.Intent
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.github.fribourgsdp.radio.*
import com.github.fribourgsdp.radio.auth.*
import com.github.fribourgsdp.radio.config.MyAppCompatActivity
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.database.DatabaseHolder
import com.github.fribourgsdp.radio.util.MyFragment
import com.google.android.gms.tasks.Task
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
const val USER_DATA = "com.github.fribourgsdp.radio.data.view.USER_DATA"

open class UserProfileActivity : MyAppCompatActivity(), KeepOrDismissPlaylistDialog.OnPickListener, MergeDismissImportPlaylistDialog.OnPickListener, DatabaseHolder {
    private lateinit var user : User
    private lateinit var usernameField : EditText
    private lateinit var usernameInitialText : TextView
    private lateinit var spotifyStatusText : TextView
    private lateinit var saveChangeButton : Button
    private lateinit var launchSpotifyButton : Button
    private lateinit var googleSignInButton : Button
    private lateinit var userIcon : ImageView
    private var signedIn : Boolean = false


    private var db = initializeDatabase()

    protected lateinit var googleSignIn : GoogleSignIn

    //firebase auth
    private lateinit var firebaseAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_profile)
        googleSignIn = GoogleSignIn(this, getString(R.string.default_web_client_id))
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
            //this behavior should change or the this button should diseapper
            updateUser()
            saveChangeButton.visibility = View.INVISIBLE
        }

        usernameField.setOnKeyListener{ v,k,e ->
            saveChangeButton.visibility = View.VISIBLE
            false
        }

        googleSignInButton.setOnClickListener {
            signInOrOut()
        }
        findViewById<FloatingActionButton>(R.id.addPlaylistButton).setOnClickListener{startActivity(Intent(this, AddPlaylistActivity::class.java))}

    }

    open fun signInOrOut(){
        if (signedIn) {
            signOut()
        } else {

            googleSignIn.signIn()
        }
    }

    private fun instantiateViews(){
        firebaseAuth = FirebaseAuth.getInstance()
        usernameField = findViewById(R.id.username)
        launchSpotifyButton = findViewById(R.id.launchSpotifyButton)
        saveChangeButton = findViewById(R.id.saveUserButton)
        usernameInitialText = findViewById(R.id.usernameInitial)
        spotifyStatusText = findViewById(R.id.spotifyStatus)
        googleSignInButton = findViewById(R.id.googleSignInButton)
        userIcon = findViewById(R.id.userIcon)
    }


    private fun updateUser(){
        user.name = usernameField.text.toString()
        //at this point, the userId should be the firebaseUser.uid
        if(user.isGoogleUser){
            //remove all n


            val onlineUser = user.onlineCopy()
            db.setUser(onlineUser.id,onlineUser)
        }else{
            val userPlaylists = user.getPlaylists()
            val userWithoutPlaylist = user
            userWithoutPlaylist.removePlaylists(userPlaylists)
            db.setUser(user.id,userWithoutPlaylist)
            user.addPlaylists(userPlaylists)
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

        signedIn = user.isGoogleUser
        if(signedIn){
            googleSignInButton.text = getString(R.string.sign_out_message)
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
        return db.getUser(user.id).continueWith {
            user.addPlaylists(it.result.getPlaylists())
            user.isGoogleUser = true
            user.name = it.result.name
            user.id = it.result.id
            val onlineUser = user.onlineCopy()
            db.setUser(onlineUser.id,onlineUser)

        }
    }
    private fun importPlaylist() : Task<Unit>{
        user.removePlaylists(user.getPlaylists())
        return db.getUser(user.id).continueWith{
            user = it.result
            user.name = it.result.name
            user.id = it.result.id
            user.isGoogleUser = true
        }
    }

    private fun dismissOnlinePlaylist() : Task<Unit>{
        return db.getUser(user.id).continueWith{
            user.name = it.result.name
            user.id = it.result.id
            user.isGoogleUser = true
            val onlineUser = user.onlineCopy()
            db.setUser(onlineUser.id,onlineUser)
        }
    }

    override fun onPick(choice: KeepOrDismissPlaylistDialog.Choice) {
        when (choice){
            KeepOrDismissPlaylistDialog.Choice.KEEP -> keepPlaylist()
            KeepOrDismissPlaylistDialog.Choice.DISMISS -> deletePlaylist()
        }.addOnSuccessListener{
            User.createDefaultUser().continueWith {
                val playlist = user.getPlaylists()
                user = it.result
                user.addPlaylists(playlist)
                user.save(this)
            }
        }

    }

    private fun keepPlaylist() : Task<Unit>{
        return Tasks.forResult(null)
    }
    private fun deletePlaylist() : Task<Unit> {
        user.removePlaylists(user.getPlaylists())
        return Tasks.forResult(null)

    }

    /**
     * Used to indicate that you are logging in from GoogleSignIn
     * @param code Represents the result of the connection through Google SignIn (Fail, newuser, normalUser or Already connected)
     */
    open fun loginFromGoogle(code : GoogleSignInResult) {
        if(code == GoogleSignInResult.NORMAL_USER || code == GoogleSignInResult.NEW_USER) {
            afterSignInProcedure()
        }
    }


    //this should only happen on successful signIn,
    private fun afterSignInProcedure(){
        user.id = firebaseAuth.currentUser?.uid ?: user.id //MAYBE THROW AN ERROR INSTEAD
        user.isGoogleUser = firebaseAuth.currentUser != null
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