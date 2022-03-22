package com.github.fribourgsdp.radio


import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

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
     * Gets the [User], wrapped in a [Task], linked to the [userId] given, [userId] should be the authentication token
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
     * Sets the [User] information in the database and link it the to [userId] given, [userId] should be the authentication token
     */
    fun setUser(userId : String, user : User){
        val userHash = hashMapOf(
            "first" to user.name
        )
        db.collection("user").document(userId).set(userHash)
    }

    /**
     * Gets a unique ID for a lobby. It is an asynchronous operation, so it is returned in a task.
     * @return a task loading a unique ID for the lobby.
     */
    fun getLobbyId() : Task<Long> {
        val keyID = "current_id"
        val keyMax = "max_id"

        val docRef = db.collection("lobby").document("id")

        return db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            val id = snapshot.getLong(keyID)!!
            val nextId = (id + 1) % snapshot.getLong(keyMax)!!

            transaction.update(docRef, keyID, nextId)

            // Success
            id
        }
    }

    fun openLobby(id: Long, settings : Game.Settings) : Task<Void> {
        val gameData = hashMapOf(
            "name" to settings.name,
            "host" to settings.host.name,
            "playlist" to settings.playlist.name,
            "nbRounds" to settings.nbRounds,
            "withHint" to settings.withHint,
            "private" to settings.isPrivate,
            "players" to listOf(settings.host.name)
        )

        return db.collection("lobby").document(id.toString())
            .set(gameData)

    }

    fun listenToLobbyUpdate(id: Long, listener: EventListener<DocumentSnapshot>) {
        db.collection("lobby").document(id.toString())
            .addSnapshotListener(listener)
    }

}