package com.github.fribourgsdp.radio.database

import android.content.ContentValues
import android.util.Log
import com.github.fribourgsdp.radio.data.*
import com.github.fribourgsdp.radio.game.Game
import com.github.fribourgsdp.radio.util.*
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.Timestamp
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.io.Serializable
import java.lang.Exception
import java.util.*


/**
 * This class serves to make possible the dependency injection with mockito. We mock the
 * reference and serve documents snapshot that are also mocked.
 *
 */
open class FirestoreRef {
    lateinit var db: FirebaseFirestore
    open fun getUserRef(userId : String) : DocumentReference {
        return db.collection("user").document(userId)
    }
    open fun getSongRef(songId : String) : DocumentReference {
        return db.collection("songs").document(songId)
    }
    open fun getPlaylistRef(playlistId : String) : DocumentReference {
        return db.collection("playlists").document(playlistId)
    }
    open fun getLobbyRef(lobbyId: String) : DocumentReference {
        return db.collection("lobby").document(lobbyId)
    }
    open fun getPublicLobbiesRef() : DocumentReference {
        return db.collection("lobby").document("public")
    }
    open fun getGenericIdRef(collectionPath : String, documentPath : String) : DocumentReference {
        return db.collection(collectionPath).document(documentPath)
    }
}

open class TransactionManager() {
    lateinit var db: FirebaseFirestore
    open fun executeIdTransaction(docRef: DocumentReference?, keyID: String, keyMax: String, number: Int) : Task<Pair<Long, Long>> {
        return db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef!!)

            if (!snapshot.exists()) {
                throw Exception("Document not found.")
            }

            val id = snapshot.getLong(keyID)!!
            val nextId = (id + number) % snapshot.getLong(keyMax)!!

            transaction.update(docRef, keyID, nextId)

            // Success
            Pair(id,nextId)
        }
    }
    open fun executeMicPermissionsTransaction(docRef: DocumentReference?, userId: String, newPermissions: Boolean, id: Long): Task<Void> {
        return db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef!!)

            if (!snapshot.exists()) {
                throw IllegalArgumentException("Document $id not found.")
            }

            val playerPermissions = snapshot.getPermissions()
            playerPermissions[userId] = newPermissions

            transaction.update(docRef, "permissions", playerPermissions)

            // Success
            null
        }
    }

    open fun openLobbyTransaction(lobbyRef: DocumentReference?, publicLobbiesRef: DocumentReference?, id: Long, gameData: HashMap<String, Serializable>?, settings: Game.Settings?): Task<Void> {
        return db.runTransaction { transaction ->
            val lobbySnapshot = transaction.get(lobbyRef!!)
            val publicLobbiesSnapshot = publicLobbiesRef?.let { transaction.get(it) }

            if (!lobbySnapshot.exists()) {
                throw IllegalArgumentException("Document $id not found.")
            }

            // If the game is public (the snapshot not null), but we can't find the public snapshot:
            // throw an exception
            if (publicLobbiesSnapshot != null && !publicLobbiesSnapshot.exists()) {
                throw IllegalArgumentException("The public lobbies could not be reached.")
            }

            transaction.set(lobbyRef, gameData!!)

            if (publicLobbiesRef != null) {
                transaction.update(
                    publicLobbiesRef, id.toString(),
                    hashMapOf(
                        "name" to settings!!.name,
                        "host" to settings!!.hostName
                    )
                )
            }
            // Success
            null
        }
    }
}
/**
 *
 *This class represents a database. It communicates with the database(Firestore)
 * and translates the result in classes
 *
 * @constructor Creates a database linked to Firestore
 */
class FirestoreDatabase(var refMake: FirestoreRef, var transactionMgr: TransactionManager) : Database {
    private val db = Firebase.firestore
    init {
        refMake.db = db
        transactionMgr.db = db
    }
    constructor():this(FirestoreRef(), TransactionManager())

    override fun getUser(userId : String): Task<User> {
        return  refMake.getUserRef(userId).get().continueWith { l ->
            val result = l.result
            if(result.exists()){
                val userId = result["userID"].toString()
                val playlists = result["playlists"] !! as ArrayList<HashMap<String,String>>
                val playlistSet : MutableSet<Playlist> = mutableSetOf()
                for(playlist in playlists){

                    val pl = Playlist(playlist["playlistName"]!!, Genre.valueOf(playlist["genre"]!!))
                    pl.id = playlist["playlistId"]!!
                    pl.savedOnline = true
                    pl.savedLocally = false
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
        Log.d(ContentValues.TAG, "DocumentSnapshot added with ID put: $docRef")
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

               val pl = Playlist(playlistTitle, songSet, Genre.valueOf(genre))
               pl.savedOnline=true
               pl
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
        return getId("lobby","id",1).continueWith { pair -> pair.result.first }
    }

    override fun generateSongIds(number: Int): Task<Pair<Long,Long>> {
        return getId("metadata", "SongInfo",number)
    }
    override fun generateUserId() : Task<Long> {
        return getId("metadata","UserInfo",1).continueWith { pair -> pair.result.first }
    }

    override fun generateSongId() : Task<Long> {
        return getId("metadata","SongInfo",1).continueWith { pair -> pair.result.first }
    }

    override fun generatePlaylistId() : Task<Long> {
        return getId("metadata", "PlaylistInfo",1).continueWith { pair -> pair.result.first }
    }


    private fun getId(collectionPath : String, documentPath : String, number : Int  ) : Task<Pair<Long,Long>>{
        val keyID = "current_id"
        val keyMax = "max_id"

        val docRef = refMake.getGenericIdRef(collectionPath, documentPath)

        return transactionMgr.executeIdTransaction(docRef, keyID, keyMax, number)
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
            "launched" to false,
            "validity" to true,
            "noSing" to settings.noSing
        )

        val lobbyRef = refMake.getLobbyRef(id.toString())

        // get the public lobbies only if the game is public
        val publicLobbiesRef = if (settings.isPrivate) null else refMake.getPublicLobbiesRef()

        return transactionMgr.openLobbyTransaction(lobbyRef, publicLobbiesRef, id, gameData, settings)
    }

    override fun listenToLobbyUpdate(id: Long, listener: EventListener<DocumentSnapshot>) {
        listenUpdate("lobby", id, listener)
    }

    override fun getGameSettingsFromLobby(id: Long) :Task<Game.Settings> {
        val docRef = refMake.getGenericIdRef("lobby", id.toString())

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
            val noSing = snapshot.getBoolean("noSing") ?: false

            // Success
            Game.Settings(host, name, playlist, nbRounds.toInt(), withHint, private, noSing)
        }
    }

    override fun addUserToLobby(id: Long, user: User, hasMicPermissions: Boolean) : Task<Void> {
        val docRef = db.collection("lobby").document(id.toString())

        return db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            if (!snapshot.exists()) {
                throw IllegalArgumentException("Document $id not found.")
            }

            val mapIdToName = snapshot.getPlayers()
            if (mapIdToName.containsKey(user.id)) {
                // A user with the same id was already added
                throw IllegalArgumentException("id: ${user.id} is already in the database")
            }

            mapIdToName[user.id] = user.name

            transaction.update(docRef, "players", mapIdToName)

            val playerPermissions = snapshot.getPermissions()
            playerPermissions[user.id] = hasMicPermissions

            transaction.update(docRef, "permissions", playerPermissions)

            // Success
            null
        }
    }

    override fun getPublicLobbies(): Task<List<LobbyData>> {
        return db.collection("lobby").document("public").get().continueWith { snapshot ->
            if (snapshot.result == null || !snapshot.result.exists()) {
                throw Exception("Could not fetch public lobbies")
            }

            createListLobbyDataFromRawData(snapshot.result.data)
        }
    }

    override fun removeLobbyFromPublic(id: Long): Task<Void> {
        return db.collection("lobby").document("public")
            .update(id.toString(), FieldValue.delete())
    }

    override fun listenToPublicLobbiesUpdate(listener: EventListener<List<LobbyData>>) {
        db.collection("lobby").document("public").addSnapshotListener { snapshot, error ->
            val value = snapshot?.let{ createListLobbyDataFromRawData(it.data) }
            listener.onEvent(value, error)
        }
    }

    override fun removeUserFromLobby(id: Long, user: User) : Task<Void> {
        val docRef = db.collection("lobby").document(id.toString())

        return db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            if (!snapshot.exists()) {
                throw IllegalArgumentException("Document $id not found.")
            }

            val mapIdToName = snapshot.get("players")!! as HashMap<String, String>
            if (!mapIdToName.containsKey(user.id)) {
                // A user with the same id was already added
                throw IllegalArgumentException("id: ${user.id} is not in the database")
            }

            mapIdToName.remove(user.id)

            transaction.update(docRef, "players", mapIdToName)

            val playerPermissions = snapshot.get("permissions")!! as HashMap<String, Boolean>
            playerPermissions.remove(user.id)

            transaction.update(docRef, "permissions", playerPermissions)

            // Success
            null
        }
    }

    override fun disableGame(id: Long): Task<Void> {
        val docRef = db.collection("games").document(id.toString())

        return db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            if (!snapshot.exists()) {
                throw IllegalArgumentException("Document $id not found.")
            }

            transaction.update(docRef, "validity", false)

            // Success
            null
        }
    }

    override fun disableLobby(gameID: Long): Task<Void> {
        val collection = db.collection("lobby")
        val lobbyRef = collection.document(gameID.toString())
        val publicRef = collection.document("public")
        return db.runTransaction { transaction ->
            val lobbySnapshot = transaction.get(lobbyRef)
            val publicLobbiesSnapshot = transaction.get(publicRef)

            if (!lobbySnapshot.exists()) {
                throw IllegalArgumentException("Document $gameID not found.")
            }

            if (!publicLobbiesSnapshot.exists()) {
                throw IllegalArgumentException("The public lobbies could not be reached.")
            }

            transaction.update(lobbyRef, "validity", false)
            transaction.update(publicRef, "$gameID", FieldValue.delete())

            // Success
            null
        }
    }

    override fun removePlayerFromGame(gameID: Long, user: User): Task<Void> {
        val docRef = db.collection("games_metadata").document(gameID.toString())

        return db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            if (!snapshot.exists()) {
                throw IllegalArgumentException("Document $gameID not found.")
            }

            val playerDoneMap = snapshot.get("player_done_map")!! as HashMap<String, Boolean>
            val playerFoundMap = snapshot.get("player_found_map")!! as HashMap<String, Boolean>
            playerDoneMap.remove(user.id)
            playerFoundMap.remove(user.id)

            transaction.update(docRef, "player_done_map", playerDoneMap)
            transaction.update(docRef, "player_found_map", playerFoundMap)

            // Success
            null
        }
    }

    override fun makeSingerDone(gameID: Long, singerId: String): Task<Void> {
        val docRef = db.collection("games_metadata").document(gameID.toString())

        return db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            if (!snapshot.exists()) {
                throw IllegalArgumentException("Document $gameID not found.")
            }

            val playerDoneMap = snapshot.get("player_done_map")!! as HashMap<String, Boolean>
            playerDoneMap[singerId] = true

            transaction.update(docRef, "player_done_map", playerDoneMap)

            // Success
            null
        }
    }

    override fun modifyUserMicPermissions(id: Long, user: User, newPermissions: Boolean): Task<Void> {
        val docRef = refMake.getLobbyRef(id.toString())
        return transactionMgr.executeMicPermissionsTransaction(docRef, user.id, newPermissions, id)
    }

    override fun openGame(id: Long): Task<Void> {
        return refMake.getGenericIdRef("games", id.toString())
            .set(
                hashMapOf(
                    "finished" to false,
                    "current_round" to 0L,
                    "singer" to "",
                    "song_choices" to ArrayList<String>(),
                    "scores" to HashMap<String, Int>(),
                    "validity" to true,
                    "song_choices_lyrics" to HashMap<String, String>(),
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
        val collection = db.collection("lobby")
        val lobbyRef = collection.document(id.toString())
        val publicRef = collection.document("public")
        return db.runTransaction { transaction ->
            val lobbySnapshot = transaction.get(lobbyRef)
            val publicLobbiesSnapshot = transaction.get(publicRef)

            if (!lobbySnapshot.exists()) {
                throw IllegalArgumentException("Document $id not found.")
            }

            if (!publicLobbiesSnapshot.exists()) {
                throw IllegalArgumentException("The public lobbies could not be reached.")
            }

            transaction.update(lobbyRef, "launched", true)
            transaction.update(publicRef, "$id", FieldValue.delete())

            // Success
            null
        }
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

    override fun updateCurrentSongOfGame(id: Long, songName: String, incrementBy: Long): Task<Void> {
        val roundDeadline = Date()
        //incrementBy is in seconds whereas we must add milliseconds to the time before deadline.
        roundDeadline.time += incrementBy*1000
        

        val songUpdateMap = hashMapOf(
            "current_song" to songName,
            "round_deadline" to Timestamp(roundDeadline)
        )

        return updateGame(id, songUpdateMap)
    }

    override fun playerEndTurn(gameID: Long, playerID: String, hasFound: Boolean): Task<Void> {
        val docRef = db.collection("games_metadata").document(gameID.toString())

        return db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            if (!snapshot.exists()) {
                throw IllegalArgumentException("Document $gameID in games not found.")
            }

            // Set the player to done
            val updatedDoneMap = snapshot.getPlayerDoneMap()
            updatedDoneMap[playerID] = true

            // Set if the player has found or not
            val updatedFoundMap = snapshot.getPlayerFoundMap()
            updatedFoundMap[playerID] = hasFound


            // Count the number of players that found the answer and compute the points if the player found
            val points = if (hasFound) Game.computeScore(
                // The position of the player:
                updatedFoundMap.count { (_, hasFound) -> hasFound }
            ) else 0

            val updatedScoreMap = snapshot.getScoresOfRound<Int>()
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
            // singer is done by default
            val updatedDoneMap = snapshot.getPlayerDoneMap()
            updatedDoneMap.replaceAll { id, _ -> id == singer}

            // reset found map
            val updatedFoundMap = snapshot.getPlayerFoundMap()
            updatedFoundMap.replaceAll { _, _ -> false}

            // reset scores of round
            val scoresOfRound = snapshot.getScoresOfRound<Long>()
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

    private fun createListLobbyDataFromRawData(data: Map<String, Any>?): List<LobbyData> {
        return data?.let {
            (it as Map<String, Map<String, String>>).entries.map { (id, data) -> LobbyData(id.toLong(), data["name"]!!, data["host"]!!) }
        } ?: ArrayList()
    }

}
