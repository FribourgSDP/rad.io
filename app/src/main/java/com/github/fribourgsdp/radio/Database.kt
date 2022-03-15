package com.github.fribourgsdp.radio

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.annotation.NonNull
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.CompletableFuture

class Database {
    private val db = Firebase.firestore


     fun getUser(userId : String): Task<User> {
         return  db.collection("user").document(userId).get().continueWith(Continuation { l-> User(l.result["first"].toString()) })
     }

    fun setUser(userId : String, user : User){
        val user = hashMapOf(
            "first" to user.name
        )
        db.collection("user").document(userId).set(user)
            .addOnSuccessListener{ documentReference ->
                //Log.d(TAG, "DocumentSnapshot added with ID: ${documentReference.id}")
                Log.d(ContentValues.TAG, "DocumentSnapshot added with ID put: ")

            }
            .addOnFailureListener{e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
    }

}