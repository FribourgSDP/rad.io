package com.github.fribourgsdp.radio

import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.EventListener
import java.lang.Exception

/**
 *
 *This class represents a database. It communicates with the database(Firestore)
 * and translates the result in classes
 *
 * @constructor Creates a database linked to Firestore
 */
class FirestoreDatabase : Database {
    private val db = Firebase.firestore


    override fun getUser(userId : String): Task<User> {
        return  db.collection("user").document(userId).get().continueWith { l ->
            val result = l.result
            if(result.exists()){
                User(result["first"].toString())
            }else{
                null

                //TODO("CREATE EXCEPTION CLASS AND THROW APPROPRIATE EXCEPTION")

            }
        }
    }


    override fun setUser(userId : String, user : User): Task<Void>{
        val userHash = hashMapOf(
            "first" to user.name
        )
        return db.collection("user").document(userId).set(userHash)
    }


    override fun getSong(songName : String): Task<Song>{
        return  db.collection("songs").document(songName).get().continueWith { l ->
            val result = l.result
            if(result.exists()){
                val songName = result["songName"].toString()
                val artistName = result["artistName"].toString()
                Song(songName,artistName,"")
            }else{
                null
            }
        }
    }

    override fun registerSong(song : Song): Task<Void>{
        val songHash = hashMapOf(
            "songName" to song.name,
            "artistName" to song.artist
            //todo: add lyrics when it won't be a future anymore
        )
        return db.collection("songs").document(song.name).set(songHash)

    }

    override fun getPlaylist(playlistName : String): Task<Playlist>{
        return  db.collection("playlists").document(playlistName).get().continueWith { l ->
            val result = l.result
            if(result.exists()){

                val playlistTitle = result["playlistName"].toString()
                val genre =result["genre"].toString()
                val songs = result["songs"].toString()

                //parse the list result to create a set
                val songSet : MutableSet<Song> = mutableSetOf()
                for(song in songs.substring(1,songs.length-1).split(",")){
                    songSet.add((Song(song,"","")))
                }

                Playlist(playlistTitle, songSet, Genre.valueOf(genre))

            }else{
                null
            }
        }
    }

    override fun registerPlaylist( playlist : Playlist): Task<Void>{
        //We will only store the songName, because you don't want to fetch all the lyrics of all songs when you retrieve the playlist
        val titleList : MutableList<String> = mutableListOf()
        for( song in playlist.getSongs()){
            titleList.add(song.name)
        }
        val playlistHash = hashMapOf(
            "playlistName" to playlist.name,
            "genre" to playlist.genre,
            "songs" to titleList
        )
        return db.collection("playlists").document(playlist.name).set(playlistHash)
    }

    override fun getLobbyId() : Task<Long> {
        return getId("lobby","id")
    }

    override fun generateUserId() : Task<Long> {
        return getId("metadata","UserInfo")
    }

    private fun getId(collectionPath : String, documentPath : String ) : Task<Long>{
        val keyID = "current_id"
        val keyMax = "max_id"

        val docRef = db.collection(collectionPath).document(documentPath)

        return db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            if (!snapshot.exists()) {
                throw Exception("Document not found.")
            }

            val id = snapshot.getLong(keyID)!!
            val nextId = (id + 1) % snapshot.getLong(keyMax)!!

            transaction.update(docRef, keyID, nextId)

            // Success
            id
        }
    }


    override fun openLobby(id: Long, settings : Game.Settings) : Task<Void> {
        val gameData = hashMapOf(
            "name" to settings.name,
            "host" to settings.hostName,
            "playlist" to settings.playlistName,
            "nbRounds" to settings.nbRounds,
            "withHint" to settings.withHint,
            "private" to settings.isPrivate,
            "players" to arrayListOf<String>(),
            "permissions" to hashMapOf<String, Boolean>(),
            "launched" to false
        )

        return db.collection("lobby").document(id.toString())
            .set(gameData)

    }

    override fun listenToLobbyUpdate(id: Long, listener: EventListener<DocumentSnapshot>) {
        listenUpdate("lobby", id, listener)
    }

    override fun getGameSettingsFromLobby(id: Long) :Task<Game.Settings> {
        val docRef = db.collection("lobby").document(id.toString())

        return db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            if (!snapshot.exists()) {
                throw IllegalArgumentException("Document $id not found.")
            }

            val host = snapshot.getString("host")!!
            val name = snapshot.getString("name")!!
            val playlist = snapshot.getString("playlist")!!
            val nbRounds = snapshot.getLong("nbRounds")!!
            val withHint = snapshot.getBoolean("withHint")!!
            val private = snapshot.getBoolean("private")!!

            // Success
            Game.Settings(host, name, playlist, nbRounds.toInt(), withHint, private)
        }
    }

    override fun addUserToLobby(id: Long, user: User, hasMicPermissions: Boolean) : Task<Void> {
        val docRef = db.collection("lobby").document(id.toString())

        return db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            if (!snapshot.exists()) {
                throw IllegalArgumentException("Document $id not found.")
            }

            val list = snapshot.get("players")!! as ArrayList<HashMap<String, String>>
            list.add(idAndName(user))

            transaction.update(docRef, "players", list)

            val playerPermissions = snapshot.get("permissions")!! as HashMap<String, Boolean>
            playerPermissions[user.name] = hasMicPermissions

            transaction.update(docRef, "permissions", playerPermissions)

            // Success
            null
        }
    }

    override fun modifyUserMicPermissions(id: Long, user: User, newPermissions: Boolean): Task<Void> {
        val docRef = db.collection("lobby").document(id.toString())

        return db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            if (!snapshot.exists()) {
                throw IllegalArgumentException("Document $id not found.")
            }

            val playerPermissions = snapshot.get("permissions")!! as HashMap<String, Boolean>
            playerPermissions[user.name] = newPermissions

            transaction.update(docRef, "permissions", playerPermissions)

            // Success
            null
        }
    }

    private fun idAndName(user: User): HashMap<String, String> {
        return hashMapOf(
            "id" to user.id,
            "name" to user.name
        )
    }

    override fun openGame(id: Long): Task<Void> {
        return db.collection("games").document(id.toString())
            .set(
                hashMapOf(
                    "current_round" to 0L,
                    "current_song" to "",
                    "singer" to "",
                    "song_choices" to ArrayList<String>()
                )
            )
    }

    override fun openGameMetadata(id: Long, usersIds: List<String>): Task<Void> {
        return db.collection("games_metadata").document(id.toString())
            .set(
                hashMapOf(
                    "player_done_map" to usersIds.associateWith { true }
                )
            )
    }

    override fun launchGame(id: Long): Task<Void> {
        return db.collection("lobby").document(id.toString())
            .update("launched", true)
    }

    override fun listenToGameUpdate(id: Long, listener: EventListener<DocumentSnapshot>) {
        listenUpdate("games", id, listener)
    }

    override fun listenToGameMetadataUpdate(id: Long, listener: EventListener<DocumentSnapshot>) {
        listenUpdate("games_metadata", id, listener)
    }

    override fun updateGame(id: Long, updatesMap: Map<String, Any>): Task<Void> {
        return db.collection("games").document(id.toString())
            .update(updatesMap)
    }

    override fun setPlayerDone(gameID: Long, playerID: String): Task<Void> {
        val docRef = db.collection("games_metadata").document(gameID.toString())

        return db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            if (!snapshot.exists()) {
                throw IllegalArgumentException("Document $gameID in games not found.")
            }

            val updatedMap = snapshot.get("player_done_map")!! as HashMap<String, Boolean>
            updatedMap[playerID] = true

            transaction.update(docRef, "player_done_map", updatedMap)

            // Success
            null
        }
    }

    override fun resetPlayerDoneMap(gameID: Long, singer: String): Task<Void> {
        val docRef = db.collection("games_metadata").document(gameID.toString())

        return db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            if (!snapshot.exists()) {
                throw IllegalArgumentException("Document $gameID in games not found.")
            }

            val updatedMap = snapshot.get("player_done_map")!! as HashMap<String, Boolean>
            updatedMap.replaceAll {k, _ -> k == singer}

            transaction.update(docRef, "player_done_map", updatedMap)

            // Success
            null
        }
    }

    private fun listenUpdate(collectionPath : String, id: Long, listener: EventListener<DocumentSnapshot>){
        db.collection(collectionPath).document(id.toString())
            .addSnapshotListener(listener)
    }


}