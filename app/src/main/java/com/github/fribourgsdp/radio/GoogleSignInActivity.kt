package com.github.fribourgsdp.radio

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.github.fribourgsdp.radio.config.MyAppCompatActivity
import com.github.fribourgsdp.radio.data.view.UserProfileActivity
import com.github.fribourgsdp.radio.databinding.ActivityGoogleSignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception


class GoogleSignInActivity : MyAppCompatActivity() {


    //view binding
    private lateinit var binding: ActivityGoogleSignInBinding

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    //constants
    private companion object {
        private const val TAG = "GOOGLE_SIGN_TAG"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoogleSignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //configure for the Google SignTn
        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)

        //init firebase auth
        firebaseAuth = FirebaseAuth.getInstance()
        checkUser()

        //Google SignIn Button, click to begin Google SignIn
        binding.googleSignInButton.setOnClickListener {

            val intent = googleSignInClient.signInIntent
            launcher.launch(intent)
        }

    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            //handle intent result here
            if (result.resultCode == Activity.RESULT_OK) {
                val accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    //Google SignIn success, now authenticate with firebase
                    val account = accountTask.getResult(ApiException::class.java)
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                    this.firebaseAuthWithCredentitial(credential, firebaseAuth)
                } catch (e: Exception) {
                    //failed Google SignIn
                    Log.d(TAG, "onActivityResult: ${e.message}")
                }
            } else {
                //cancelled
                Toast.makeText(this@GoogleSignInActivity, "Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    private fun checkUser() {
        //check if user is logged in or not
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            //user is already logged in
            //start profile activity
            val intent = Intent(this@GoogleSignInActivity, UserProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    fun firebaseAuthWithCredentitial(credential: AuthCredential, firebaseAuth: FirebaseAuth) {
        firebaseAuth.signInWithCredential(credential).addOnSuccessListener { authResult ->
            //login success
            //check if user is new or existing
            if (authResult.additionalUserInfo!!.isNewUser) {
                //user is new - Account Create
               // val mail = saveUserInDatabse(authResult)
                //saveTestUser(this, mail)
                Toast.makeText(this@GoogleSignInActivity, "Account created", Toast.LENGTH_SHORT)
                    .show()
            } else {
                //existing user - LoggedIn
                Toast.makeText(this@GoogleSignInActivity, "LoggedIn", Toast.LENGTH_SHORT).show()
            }
            //start profile activity
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
            .addOnFailureListener { e ->
                //login failed
                Toast.makeText(
                    this@GoogleSignInActivity, "Loggin Failed due to ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, UserProfileActivity::class.java)
        startActivity(intent)
        finish()
    }
}

/*
private fun saveTestUser(context : Context, mail : String){
    /** this user allows quick demo's as it is data that is written to the app
     * specific storage and can be easily read without intents */
    val mockUser = User(mail, User.generateColor())
    mockUser.save(context)
}

private fun saveUserInDatabse(authResult : AuthResult) : String{
    val firebaseUser = authResult.user
    val db = FirestoreDatabase()
    val id = firebaseUser!!.uid
    val mail = firebaseUser.email
    val user = User(mail!!)
    db.setUser(id, user)
    return mail
}*/