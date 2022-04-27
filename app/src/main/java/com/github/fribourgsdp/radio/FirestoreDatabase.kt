package com.github.fribourgsdp.radio

import android.content.ContentValues
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.EventListener
import java.lang.Exception

/**
 * This class serves to make possible the dependency injection with mockito. We mock the
 * reference and serve documents snapshot that are also mocked.
 *
 */
open class FirestoreRef {
    val db = Firebase.firestore
    open fun getUserRef(userId : String) : DocumentReference{
        return db.collection("user").document(userId)
    }
    open fun getSongRef(songId : String) : DocumentReference{
        return db.collection("songs").document(songId)
    }
    open fun getPlaylistRef(playlistId : String) : DocumentReference{
        return db.collection("playlists").document(playlistId)
    }
}
/**
 *
 *This class represents a database. It communicates with the database(Firestore)
 * and translates the result in classes
 *
 * @constructor Creates a database linked to Firestore
 */
class FirestoreDatabase(var refMake: FirestoreRef) : Database {
    private val db = Firebase.firestore
    constructor():this(FirestoreRef())

    override fun getUser(userId : String): Task<User> {
        return  refMake.getUserRef(userId).get().continueWith { l ->
            val result = l.result
            if(result.exists()){
                val userId = result["userID"].toString()
                val playlists = result["playlists"] !! as ArrayList<HashMap<String,String>>
                val playlistSet : MutableSet<Playlist> = mutableSetOf()
                for(playlist in playlists){

                    val pl = Playlist(playlist["playlistName"]!!,Genre.valueOf(playlist["genre"]!!))
                    pl.id = playlist["playlistId"]!!
                    playlistSet.add(pl)
                }

                val name = result["username"].toString()
                val user = User(name)
                user.addPlaylists(playlistSet)
                user.id = userId
                user
            }else{
                null
                //TODO("CREATE EXCEPTION CLASS AND THROW APPROPRIATE EXCEPTION")

            }
        }
    }


    override fun setUser(userId : String, user : User): Task<Void>{

        val playlistInfo : MutableList<HashMap<String,String>> = mutableListOf()
        for ( playlist in user.getPlaylists()){
            playlistInfo.add((hashMapOf("playlistName" to playlist.name,"playlistId" to playlist.id, "genre" to playlist.genre.toString())))
        }
        val userHash = hashMapOf(
            "username" to user.name,
            "userID" to user.id,
            "playlists" to playlistInfo
        )
        val docRef = refMake.getUserRef(userId)
        Log.d(ContentValues.TAG, "DocumentSnapshot added with ID put: " + docRef.toString())
        return docRef.set(userHash)
    }


    override fun getSong(songId : String): Task<Song>{
        if(songId == ""){
            return Tasks.forException(IllegalArgumentException("Not null id is expected"))
        }
        return  refMake.getSongRef(songId).get().continueWith { l ->
            val result = l.result
            if(result.exists()){
                val songName = result["songName"].toString()
                val artistName = result["artistName"].toString()
                val lyrics = result["lyrics"].toString()
                val id = result["songId"].toString()
                Song(songName,artistName,lyrics,id)
            }else{
                null
            }
        }
    }

    override fun registerSong(song : Song): Task<Void>{
            if(song.id == ""){
                return Tasks.forException(IllegalArgumentException("Not null id is expected"))
            }
            val songHash = hashMapOf(
                "songId" to song.id,
                "songName" to song.name,
                "artistName" to song.artist,
                "lyrics" to song.lyrics
            )
            return refMake.getSongRef(song.id).set(songHash)

    }

    override fun getPlaylist(playlistId : String): Task<Playlist>{
        if(playlistId == ""){
            return Tasks.forException(IllegalArgumentException("Not null id is expected"))
        }
        return  refMake.getPlaylistRef(playlistId).get().continueWith { l ->
            val result = l.result
            if(result.exists()){
                val playlistTitle = result["playlistName"].toString()
                val genre =result["genre"].toString()
                val songs = result["songs"] !! as ArrayList<HashMap<String,String>>
                val songSet : MutableSet<Song> = mutableSetOf()
                for(song in songs){
                    val songEntry = (Song(song["songName"]!!,song["songArtist"]!!,""))
                    songEntry.id = song["songId"]!!
                    songSet.add(songEntry)
                }

                Playlist(playlistTitle, songSet, Genre.valueOf(genre))
            }else{
                null
            }
        }
    }

    override fun registerPlaylist( playlist : Playlist): Task<Void>{
        //We will only store the songName, because you don't want to fetch all the lyrics of all songs when you retrieve the playlist
        if(playlist.id == ""){
            return Tasks.forException(IllegalArgumentException("Not null id is expected"))
        }
        val titleList : MutableList<HashMap<String,String>> = mutableListOf()
        for( song in playlist.getSongs()){
            if(song.id == ""){
                return Tasks.forException(IllegalArgumentException("Not null id is expected"))
            }
            titleList.add(hashMapOf("songId" to song.id,"songName" to song.name,"songArtist" to song.artist))
        }
        val playlistHash = hashMapOf(
            "playlistId" to playlist.id,
            "playlistName" to playlist.name,
            "genre" to playlist.genre,
            "songs" to titleList
        )
        return refMake.getPlaylistRef(playlist.id).set(playlistHash)
    }

    override fun getLobbyId() : Task<Long> {
        return getId("lobby","id")
    }

    override fun generateUserId() : Task<Long> {
        return getId("metadata","UserInfo")
    }

    override fun generateSongId() : Task<Long> {
        return getId("metadata","SongInfo")
    }

    override fun generatePlaylistId() : Task<Long> {
        return getId("metadata", "PlaylistInfo")
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
            "players" to hashMapOf<String, String>(),
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

            val mapIdToName = snapshot.get("players")!! as HashMap<String, String>
            if (mapIdToName.containsKey(user.id)) {
                // A user with the same id was already added
                throw IllegalArgumentException("id: ${user.id} is already in the database")
            }

            mapIdToName[user.id] = user.name

            transaction.update(docRef, "players", mapIdToName)

            val playerPermissions = snapshot.get("permissions")!! as HashMap<String, Boolean>
            playerPermissions[user.id] = hasMicPermissions

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
            playerPermissions[user.id] = newPermissions

            transaction.update(docRef, "permissions", playerPermissions)

            // Success
            null
        }
    }

    override fun openGame(id: Long): Task<Void> {
        return db.collection("games").document(id.toString())
            .set(
                hashMapOf(
                    "finished" to false,
                    "current_round" to 0L,
                    "current_song" to "",
                    "singer" to "",
                    "song_choices" to ArrayList<String>(),
                    "song_choices_lyrics" to HashMap<String, String>(),
                    "scores" to HashMap<String, Int>()
                )
            )
    }

    override fun openGameMetadata(id: Long, usersIds: List<String>): Task<Void> {
        return db.collection("games_metadata").document(id.toString())
            .set(
                hashMapOf(
                    "player_done_map" to usersIds.associateWith { true },
                    "player_found_map" to usersIds.associateWith { false },
                    "scores_of_round" to usersIds.associateWith { 0 }
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

    override fun playerEndTurn(gameID: Long, playerID: String, hasFound: Boolean): Task<Void> {
        val docRef = db.collection("games_metadata").document(gameID.toString())

        return db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            if (!snapshot.exists()) {
                throw IllegalArgumentException("Document $gameID in games not found.")
            }

            // Set the player to done
            val updatedDoneMap = snapshot.get("player_done_map")!! as HashMap<String, Boolean>
            updatedDoneMap[playerID] = true

            // Set if the player has found or not
            val updatedFoundMap = snapshot.get("player_found_map")!! as HashMap<String, Boolean>
            updatedFoundMap[playerID] = hasFound


            // Count the number of players that found the answer and compute the points
            val points = Game.computeScore(
                // The position of the player:
                updatedFoundMap.count { (_, hasFound) -> hasFound }
            )

            val updatedScoreMap = snapshot.get("scores_of_round")!! as HashMap<String, Int>
            updatedScoreMap[playerID] = points

            // Update on database
            transaction.update(
                docRef,
                "player_done_map", updatedDoneMap,
                "player_found_map", updatedFoundMap,
                "scores_of_round", updatedScoreMap
            )

            // Success
            null

        }
    }

    override fun resetGameMetadata(gameID: Long, singer: String): Task<Void> {
        val docRef = db.collection("games_metadata").document(gameID.toString())

        return db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            if (!snapshot.exists()) {
                throw IllegalArgumentException("Document $gameID in games not found.")
            }

            // reset done map
            val updatedDoneMap = snapshot.get("player_done_map")!! as HashMap<String, Boolean>
            updatedDoneMap.replaceAll { k, _ -> k == singer}

            // reset found map
            val updatedFoundMap = snapshot.get("player_found_map")!! as HashMap<String, Boolean>
            updatedFoundMap.replaceAll { _, _ -> false}

            // reset scores of round
            val scoresOfRound = snapshot.get("scores_of_round")!! as HashMap<String, Long>
            scoresOfRound.replaceAll { _, _ -> 0L}

            // Update on database
            transaction.update(
                docRef,
                "player_done_map", updatedDoneMap,
                "player_found_map", updatedFoundMap,
                "scores_of_round", scoresOfRound
            )

            // Success
            null
        }
    }

    private fun listenUpdate(collectionPath : String, id: Long, listener: EventListener<DocumentSnapshot>){
        db.collection(collectionPath).document(id.toString())
            .addSnapshotListener(listener)
    }


}