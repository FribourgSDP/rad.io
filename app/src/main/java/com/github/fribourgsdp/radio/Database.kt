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
import java.lang.Exception

import java.util.concurrent.CompletableFuture

/**
 *
 *This class represents a database. It communicates with the database(Firestore)
 * and translates the result in classes
 *
 * @constructor Creates a database linked to Firestore
 */
class Database {
    private val db = Firebase.firestore

    /**
     * Gets the [User], wrapped in a [Task], linked to the [userId] given.
     * @return [User] wrapped in a task, if the [userId] doesn't exists, it returns a null [User]
     *
     */
     fun getUser(userId : String): Task<User> {

         return  db.collection("user").document(userId).get().continueWith { l ->
             val found = l.result["first"].toString()
             if (found == "null"){
                 null
             }else{
                 User(found)
             }
         }
    }

    /**
     * Sets the [User] information in the database and link it the to [userId] given
     */
    fun setUser(userId : String, user : User){
        val userHash = hashMapOf(
            "first" to user.name
        )
        db.collection("user").document(userId).set(userHash)
    }

}