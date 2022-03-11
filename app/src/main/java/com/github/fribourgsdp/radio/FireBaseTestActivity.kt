package com.github.fribourgsdp.radio

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


        val db = Firebase.firestore
        val userAuthUID = "m0sd9l"//FirebaseAuth.getInstance().currentUser?.uid
        // Create a new user with a first and last name
        val user = hashMapOf(
            "first" to "Nathan",
            "last" to "Lovelace",
            "born" to 1815,
            "friend" to arrayListOf("lucien","victor")
        )

// Add a new document with a generated ID
        db.collection("users").document(userAuthUID).set(user)
            //.add(user)
            .addOnSuccessListener { documentReference ->
                //Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                Log.d(TAG, "DocumentSnapshot added with ID: ")
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding document", e)
            }




        val cities = db.collection("cities")

        val data1 = hashMapOf(
            "name" to "San Francisco",
            "state" to "CA",
            "country" to "USA",
            "capital" to false,
            "population" to 860000,
            "regions" to listOf("west_coast", "norcal")
        )
        cities.document("SF").set(data1)

        val docRef = db.collection("users").document(userAuthUID)
        val simpleEditText = findViewById<TextView>(R.id.textToChange)
        docRef.get().addOnSuccessListener { documentSnapshot ->
            //val city = documentSnapshot.name;
            simpleEditText.text =  documentSnapshot["first"].toString()
            //simpleEditText.text = documentSnapshot.get("friend").toString().split(",")[0]
            //val list = documentSnapshot["friend"]
           // val x = list as Array<String>
            //simpleEditText.text =  x[0]
            //val strValue = simpleEditText.text.toString()
        }


    }
}