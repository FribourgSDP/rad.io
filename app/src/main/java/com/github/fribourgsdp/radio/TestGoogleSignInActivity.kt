package com.github.fribourgsdp.radio

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.github.fribourgsdp.radio.databinding.ActivityTestGoogleSignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception


class TestGoogleSignInActivity : AppCompatActivity() {


    //view binding
    private lateinit var binding: ActivityTestGoogleSignInBinding

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    //constants
    private companion object{
        private const val TAG = "GOOGLE_SIGN_TAG"
    }




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestGoogleSignInBinding.inflate(layoutInflater)
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
        binding.googleSignInButton.setOnClickListener{
            Log.d(TAG,"onCreate: begin Google SignIn")
            val intent = googleSignInClient.signInIntent
            launcher.launch(intent)
        }

    }

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            result ->
            //handle intent result here
            if(result.resultCode == Activity.RESULT_OK){
                Log.d(TAG, "onActivityResult: Google SignIn intent result")
                val accountTask = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try{
                    //Google SignIn success, now authenticate with firebase
                    val account = accountTask.getResult(ApiException::class.java)
                    firebaseAuthWithGoogleAccount(account)
                }
                catch (e :Exception){
                    //failed Google SignIn
                    Log.d(TAG, "onActivityResult: ${e.message}")
                }
            }else{
                //cancelled
                Toast.makeText(this@TestGoogleSignInActivity,"Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    private fun checkUser(){
        //check if user is logged in or not
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null){
            //user is already logged in
            //start profile activity
            startActivity(Intent(this@TestGoogleSignInActivity, ProfileActivity::class.java))
            finish()
        }

    }


    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount){
        Log.d(TAG, "firebaseAuthWithGoogleAccount: begin firebase auth with google account")

        val credential  = GoogleAuthProvider.getCredential(account!!.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener { authResult->
                //login success
                Log.d(TAG, "firebaseAuthWithGoogleAccount: LoggedIn")

                //get loggedIn user
                val firebaseUser = firebaseAuth.currentUser
                //get user info
                val uid = firebaseAuth!!.uid
                val email = firebaseUser!!.email

                Log.d(TAG, "firebaseAuthWithGoogleAccount: Uid: $uid")
                Log.d(TAG, "firebaseAuthWithGoogleAccount: Email: $email")

                //check if user is new or existing
                if(authResult.additionalUserInfo!!.isNewUser) {
                    //user is new - Account Created
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Account created... \n$email")
                    Toast.makeText(this@TestGoogleSignInActivity,"Account created... \n" +
                            "$email", Toast.LENGTH_SHORT).show()

                }else{
                    //existing user - LoggedIn
                    Log.d(TAG, "firebaseAuthWithGoogleAccount: Existing User... \n$email")
                    Toast.makeText(this@TestGoogleSignInActivity,"LoggedIn... \n" +
                            "$email", Toast.LENGTH_SHORT).show()
                }
                //start profile activity
                startActivity(Intent(this@TestGoogleSignInActivity, ProfileActivity::class.java))
                finish()

            }
            .addOnFailureListener{e->
                //login failed
                Log.d(TAG, "firebaseAuthWithGoogleAccount: Loggin Failed due to ${e.message}")
                Toast.makeText(this@TestGoogleSignInActivity,"Loggin Failed due to ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }

    }



}