package com.github.fribourgsdp.radio

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.github.fribourgsdp.radio.databinding.ActivityGoogleSignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception


class GoogleSignInActivity : AppCompatActivity() {


    //view binding
    private lateinit var binding: ActivityGoogleSignInBinding

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var firebaseAuth: FirebaseAuth

    //constants
    private companion object{
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
        binding.googleSignInButton.setOnClickListener{

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
                    val credential  = GoogleAuthProvider.getCredential(account.idToken, null)
                    this.firebaseAuthWithCredentitial(credential, firebaseAuth)
                }
                catch (e :Exception){
                    //failed Google SignIn
                    Log.d(TAG, "onActivityResult: ${e.message}")
                }
            }else{
                //cancelled
                Toast.makeText(this@GoogleSignInActivity,"Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

    private fun checkUser(){
        //check if user is logged in or not
        val firebaseUser = firebaseAuth.currentUser
        if(firebaseUser != null){
            //user is already logged in
            //start profile activity
            startActivity(Intent(this@GoogleSignInActivity, UserProfileActivity::class.java))
            finish()
        }

    }


    fun firebaseAuthWithCredentitial(credential: AuthCredential, firebaseAuth : FirebaseAuth){
        //Log.d(TAG, "firebaseAuthWithGoogleAccount: begin firebase auth with google account")


        firebaseAuth.signInWithCredential(credential).addOnSuccessListener { authResult->
                //login success
            Log.d(TAG, "ture")
                //check if user is new or existing
                if(authResult.additionalUserInfo!!.isNewUser) {
                    //user is new - Account Create
                    Toast.makeText(this@GoogleSignInActivity,"Account created", Toast.LENGTH_SHORT).show()

                }else{
                    //existing user - LoggedIn
                    Toast.makeText(this@GoogleSignInActivity,"LoggedIn", Toast.LENGTH_SHORT).show()
                }
                //start profile activity
                val intent : Intent = Intent(this, UserProfileActivity::class.java).apply {
                    putExtra(USERNAME, "Default")
                }
                startActivity(intent)
                finish()

            }
            .addOnFailureListener{e->
                //login failed
                Log.d(TAG, "false")
                Toast.makeText(this@GoogleSignInActivity,"Loggin Failed due to ${e.message}",
                    Toast.LENGTH_SHORT).show()
            }

    }

}

