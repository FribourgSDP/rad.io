package com.github.fribourgsdp.radio

import android.content.ContentValues
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FireBaseTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fire_base_test)


        val db = Database()

        val userAuthUID = "m0sd9l"//FirebaseAuth.getInstance().currentUser?.uid
        // Create a new user with a first and last name
        val userNa = User("nathanDuchesne")
        db.setUser(userAuthUID,userNa)

        db.getUser("m0sd9l").addOnSuccessListener { l ->
            if (l == null){
                findViewById<TextView>(R.id.textToChange).text = "There has been an error"

            }else {
                findViewById<TextView>(R.id.textToChange).text = l.name
            }
        }.addOnFailureListener { e ->
            Log.w(ContentValues.TAG, "Error adding document", e)
        }
    }
}




