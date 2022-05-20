package com.github.fribourgsdp.radio.auth

import android.app.Activity
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.data.view.UserProfileActivity
import com.github.fribourgsdp.radio.external.google.GoogleSignInActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception



class GoogleSignIn(val activity: UserProfileActivity, serverClientId : String) {

    private val googleSignInOptions : GoogleSignInOptions
    private val googleSignInClient : GoogleSignInClient
    private val launcher : ActivityResultLauncher<Intent>
    private lateinit var firebaseAuth : FirebaseAuth

    private companion object {
        private const val TAG = "GOOGLE_SIGN_TAG"
    }

    init{
        googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(serverClientId)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(activity, googleSignInOptions)


        launcher =
            activity.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
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
                    Toast.makeText(activity, "Cancelled", Toast.LENGTH_SHORT).show()
                }
            }
    }

    fun signIn(firebaseAuth : FirebaseAuth = FirebaseAuth.getInstance()){
        //init firebase auth
        this.firebaseAuth = firebaseAuth

        if(checkUser()) {
            activity.loginFromGoogle(GoogleSignInResult.ALREADY_LOGIN)
        }else{
            val intent = googleSignInClient.signInIntent
            launcher.launch(intent)
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
                Toast.makeText(activity, "Account created", Toast.LENGTH_SHORT)
                    .show()

                activity.loginFromGoogle(GoogleSignInResult.NEW_USER)
            } else {
                //existing user - LoggedIn
                Toast.makeText(activity, "LoggedIn", Toast.LENGTH_SHORT).show()

                activity.loginFromGoogle(GoogleSignInResult.NORMAL_USER)
            }


        }
            .addOnFailureListener { e ->
                //login failed
                Toast.makeText(
                    activity, "Loggin Failed due to ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()

                activity.loginFromGoogle(GoogleSignInResult.FAIL)
            }
    }


    private fun checkUser() : Boolean{
        //check if user is logged in or not
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            //user is already logged in
            //start profile activity
            return true
        }
        return false
    }

}