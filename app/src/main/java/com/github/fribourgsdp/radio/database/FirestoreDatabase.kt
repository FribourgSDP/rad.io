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
        return db.collection(USER_COLLECTION_PATH).document(userId)
    }
    open fun getSongRef(songId : String) : DocumentReference {
        return db.collection(SONGS_COLLECTION_PATH).document(songId)
    }
    open fun getPlaylistRef(PLAYLIST_ID_KEY : String) : DocumentReference {
        return db.collection(PLAYLISTS_COLLECTION_PATH).document(PLAYLIST_ID_KEY)
    }
    open fun getLobbyRef(lobbyId: String) : DocumentReference {
        return db.collection(LOBBY_COLLECTION_PATH).document(lobbyId)
    }
    open fun getPublicLobbiesRef() : DocumentReference {
        return db.collection(LOBBY_COLLECTION_PATH).document(PUBLIC_DOCUMENT_PATH)
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

            transaction.update(docRef, PERMISSIONS_KEY, playerPermissions)

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
                        NAME_KEY to settings!!.name,
                        HOST_KEY to settings.hostName
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
    private var gameListerRegistration : ListenerRegistration? = null
    private var metadataGameListerRegistration : ListenerRegistration? = null
    private var lobbyListerRegistration : ListenerRegistration? = null
    private var publicLobbyListerRegistration : ListenerRegistration? = null

    init {
        refMake.db = db
        transactionMgr.db = db
    }
    constructor():this(FirestoreRef(), TransactionManager())

    override fun getUser(userId : String): Task<User> {
        return  refMake.getUserRef(userId).get().continueWith { l ->
            val result = l.result
            if(result.exists()){
                val userId = result[USER_ID_KEY].toString()
                val playlists = result[PLAYLISTS_COLLECTION_PATH] !! as ArrayList<HashMap<String,String>>
                val playlistSet : MutableSet<Playlist> = mutableSetOf()
                for(playlist in playlists){

                    val pl = Playlist(playlist[PLAYLIST_NAME_KEY]!!, Genre.valueOf(playlist[GENRE_KEY]!!))
                    pl.id = playlist[PLAYLIST_ID_KEY]!!
                    pl.savedOnline = true
                    pl.savedLocally = false
                    playlistSet.add(pl)
                }

                val name = result[USERNAME_KEY].toString()
                val user = User(name)
                user.addPlaylists(playlistSet)
                user.id = userId
                user
            }else{
                null

            }
        }
    }


    override fun setUser(userId : String, user : User): Task<Void>{

        val playlistInfo : MutableList<HashMap<String,String>> = mutableListOf()
        for ( playlist in user.getPlaylists()){
            playlistInfo.add((hashMapOf(PLAYLIST_NAME_KEY to playlist.name,PLAYLIST_ID_KEY to playlist.id, GENRE_KEY to playlist.genre.toString())))
        }
        val userHash = hashMapOf(
            USERNAME_KEY to user.name,
            USER_ID_KEY to user.id,
            PLAYLISTS_COLLECTION_PATH to playlistInfo
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
                val songName = result[SONG_NAME_KEY].toString()
                val artistName = result[ARTIST_NAME_KEY].toString()
                val lyrics = result[LYRICS_KEY].toString()
                val id = result[SONG_ID_KEY].toString()
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
                SONG_ID_KEY to song.id,
                SONG_NAME_KEY to song.name,
                ARTIST_NAME_KEY to song.artist,
                LYRICS_KEY to song.lyrics
            )
            return refMake.getSongRef(song.id).set(songHash)

    }

    override fun getPlaylist(playlistName : String): Task<Playlist>{
        if(playlistName == ""){
            return Tasks.forException(IllegalArgumentException("Not null id is expected"))
        }
        return  refMake.getPlaylistRef(playlistName).get().continueWith { l ->
            val result = l.result
            if(result.exists()){
                val playlistTitle = result[PLAYLIST_NAME_KEY].toString()
                val genre = result[GENRE_KEY].toString()
                val songs = result[SONGS_COLLECTION_PATH] !! as ArrayList<HashMap<String,String>>
                val songSet : MutableSet<Song> = mutableSetOf()
                for(song in songs){
                    val songEntry = (Song(song[SONG_NAME_KEY]!!,song["songArtist"]!!,""))
                    songEntry.id = song[SONG_ID_KEY]!!
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
            titleList.add(hashMapOf(SONG_ID_KEY to song.id,SONG_NAME_KEY to song.name,"songArtist" to song.artist))
        }
        val playlistHash = hashMapOf(
            PLAYLIST_ID_KEY to playlist.id,
            PLAYLIST_NAME_KEY to playlist.name,
            GENRE_KEY to playlist.genre,
            SONGS_COLLECTION_PATH to titleList
        )
        return refMake.getPlaylistRef(playlist.id).set(playlistHash)
    }

    override fun getLobbyId() : Task<Long> {
        return getId(LOBBY_COLLECTION_PATH, ID_DOCUMENT_PATH,1).continueWith { pair -> pair.result.first }
    }

    override fun generateSongIds(number: Int): Task<Pair<Long,Long>> {
        return getId(METADATA_COLLECTION_PATH, SONG_INFO_DOCUMENT_PATH,number)
    }
    override fun generateUserId() : Task<Long> {
        return getId(METADATA_COLLECTION_PATH, USER_INFO_DOCUMENT_PATH,1).continueWith { pair -> pair.result.first }
    }

    override fun generateSongId() : Task<Long> {
        return getId(METADATA_COLLECTION_PATH, SONG_INFO_DOCUMENT_PATH,1).continueWith { pair -> pair.result.first }
    }

    override fun generatePlaylistId() : Task<Long> {
        return getId(METADATA_COLLECTION_PATH, PLAYLIST_INFO_DOCUMENT_PATH,1).continueWith { pair -> pair.result.first }
    }


    private fun getId(collectionPath : String, documentPath : String, number : Int  ) : Task<Pair<Long,Long>>{
        val keyID = "current_id"
        val keyMax = "max_id"

        val docRef = refMake.getGenericIdRef(collectionPath, documentPath)

        return transactionMgr.executeIdTransaction(docRef, keyID, keyMax, number)
    }




    override fun openLobby(id: Long, settings : Game.Settings) : Task<Void> {
        val gameData = hashMapOf(
            NAME_KEY to settings.name,
            HOST_KEY to settings.hostName,
            PLAYLIST_KEY to settings.playlistName,
            NB_ROUNDS_KEY to settings.nbRounds,
            WITH_HINT_KEY to settings.withHint,
            PRIVATE_KEY to settings.isPrivate,
            SINGER_DURATION_KEY to settings.singerDuration,
            PLAYERS_KEY to hashMapOf<String, String>(),
            PERMISSIONS_KEY to hashMapOf<String, Boolean>(),
            LAUNCHED_KEY to false,
            VALIDITY_KEY to true,
            NO_SING_KEY to settings.noSing
        )

        val lobbyRef = refMake.getLobbyRef(id.toString())

        // get the public lobbies only if the game is public
        val publicLobbiesRef = if (settings.isPrivate) null else refMake.getPublicLobbiesRef()

        return transactionMgr.openLobbyTransaction(lobbyRef, publicLobbiesRef, id, gameData, settings)
    }

    override fun listenToLobbyUpdate(id: Long, listener: EventListener<DocumentSnapshot>) {
        removeLobbyListener()
        lobbyListerRegistration = listenUpdate(LOBBY_COLLECTION_PATH, id, listener)
    }

    override fun getGameSettingsFromLobby(id: Long) :Task<Game.Settings> {
        val docRef = refMake.getGenericIdRef(LOBBY_COLLECTION_PATH, id.toString())

        return db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            if (!snapshot.exists()) {
                throw IllegalArgumentException("Document $id not found.")
            }

            val host = snapshot.getString(HOST_KEY)!!
            val name = snapshot.getString(NAME_KEY)!!
            val playlist = snapshot.getString(PLAYLIST_KEY)!!
            val nbRounds = snapshot.getLong(NB_ROUNDS_KEY)!!
            val singerDuration = snapshot.getLong(SINGER_DURATION_KEY)!!
            val withHint = snapshot.getBoolean(WITH_HINT_KEY)!!
            val private = snapshot.getBoolean(PRIVATE_KEY)!!
            val noSing = snapshot.getBoolean(NO_SING_KEY) ?: false

            // Success
            Game.Settings(host, name, playlist, nbRounds.toInt(), withHint, private, singerDuration, noSing)
        }
    }

    override fun addUserToLobby(id: Long, user: User, hasMicPermissions: Boolean) : Task<Void> {
        val docRef = db.collection(LOBBY_COLLECTION_PATH).document(id.toString())

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

            transaction.update(docRef, PLAYERS_KEY, mapIdToName)

            val playerPermissions = snapshot.getPermissions()
            playerPermissions[user.id] = hasMicPermissions

            transaction.update(docRef, PERMISSIONS_KEY, playerPermissions)

            // Success
            null
        }
    }

    override fun getPublicLobbies(): Task<List<LobbyData>> {
        return db.collection(LOBBY_COLLECTION_PATH).document(PUBLIC_DOCUMENT_PATH).get().continueWith { snapshot ->
            if (snapshot.result == null || !snapshot.result.exists()) {
                throw Exception("Could not fetch public lobbies")
            }

            createListLobbyDataFromRawData(snapshot.result.data)
        }
    }

    override fun removeLobbyFromPublic(id: Long): Task<Void> {
        return db.collection(LOBBY_COLLECTION_PATH).document(PUBLIC_DOCUMENT_PATH)
            .update(id.toString(), FieldValue.delete())
    }

    override fun listenToPublicLobbiesUpdate(listener: EventListener<List<LobbyData>>) {
        removePublicLobbyListener()
        publicLobbyListerRegistration = db.collection(LOBBY_COLLECTION_PATH).document(PUBLIC_DOCUMENT_PATH).addSnapshotListener { snapshot, error ->
            val value = snapshot?.let{ createListLobbyDataFromRawData(it.data) }
            listener.onEvent(value, error)
        }
    }

    override fun removeUserFromLobby(id: Long, user: User) : Task<Void> {
        val docRef = db.collection(LOBBY_COLLECTION_PATH).document(id.toString())

        return db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            if (!snapshot.exists()) {
                throw IllegalArgumentException("Document $id not found.")
            }

            val mapIdToName = snapshot.getAndCast<HashMap<String, String>>(PLAYERS_KEY)
            if (!mapIdToName.containsKey(user.id)) {
                // A user with the same id was already added
                throw IllegalArgumentException("id: ${user.id} is not in the database")
            }

            mapIdToName.remove(user.id)

            transaction.update(docRef, PLAYERS_KEY, mapIdToName)

            val playerPermissions = snapshot.getAndCast<HashMap<String, Boolean>>(PERMISSIONS_KEY)
            playerPermissions.remove(user.id)

            transaction.update(docRef, PERMISSIONS_KEY, playerPermissions)

            // Success
            null
        }
    }

    override fun disableGame(id: Long): Task<Void> {
        val docRef = db.collection(GAMES_COLLECTION_PATH).document(id.toString())

        return db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            if (!snapshot.exists()) {
                throw IllegalArgumentException("Document $id not found.")
            }

            transaction.update(docRef, VALIDITY_KEY, false)

            // Success
            null
        }
    }

    override fun disableLobby(gameID: Long): Task<Void> {
        val collection = db.collection(LOBBY_COLLECTION_PATH)
        val lobbyRef = collection.document(gameID.toString())
        val publicRef = collection.document(PUBLIC_DOCUMENT_PATH)
        return db.runTransaction { transaction ->
            val lobbySnapshot = transaction.get(lobbyRef)
            val publicLobbiesSnapshot = transaction.get(publicRef)

            if (!lobbySnapshot.exists()) {
                throw IllegalArgumentException("Document $gameID not found.")
            }

            if (!publicLobbiesSnapshot.exists()) {
                throw IllegalArgumentException("The public lobbies could not be reached.")
            }

            transaction.update(lobbyRef, VALIDITY_KEY, false)
            transaction.update(publicRef, "$gameID", FieldValue.delete())

            // Success
            null
        }
    }

    override fun removePlayerFromGame(gameID: Long, user: User): Task<Void> {
        val docRef = db.collection(GAMES_METADATA_COLLECTION_PATH).document(gameID.toString())

        return db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            if (!snapshot.exists()) {
                throw IllegalArgumentException("Document $gameID not found.")
            }

            val playerDoneMap = snapshot.getAndCast<HashMap<String, Boolean>>(PLAYER_DONE_MAP_KEY)
            val playerFoundMap = snapshot.getAndCast<HashMap<String, Boolean>>(PLAYER_FOUND_MAP_KEY)
            playerDoneMap.remove(user.id)
            playerFoundMap.remove(user.id)

            transaction.update(docRef, PLAYER_DONE_MAP_KEY, playerDoneMap)
            transaction.update(docRef, PLAYER_FOUND_MAP_KEY, playerFoundMap)

            // Success
            null
        }
    }

    override fun makeSingerDone(gameID: Long, singerId: String): Task<Void> {
        val docRef = db.collection(GAMES_METADATA_COLLECTION_PATH).document(gameID.toString())

        return db.runTransaction { transaction ->
            val snapshot = transaction.get(docRef)

            if (!snapshot.exists()) {
                throw IllegalArgumentException("Document $gameID not found.")
            }

            val playerDoneMap = snapshot.getAndCast<HashMap<String, Boolean>>(PLAYER_DONE_MAP_KEY)
            playerDoneMap[singerId] = true

            transaction.update(docRef, PLAYER_DONE_MAP_KEY, playerDoneMap)

            // Success
            null
        }
    }

    override fun modifyUserMicPermissions(id: Long, user: User, newPermissions: Boolean): Task<Void> {
        val docRef = refMake.getLobbyRef(id.toString())
        return transactionMgr.executeMicPermissionsTransaction(docRef, user.id, newPermissions, id)
    }

    override fun openGame(id: Long): Task<Void> {
        return refMake.getGenericIdRef(GAMES_COLLECTION_PATH, id.toString())
            .set(
                hashMapOf(
                    FINISHED_KEY to false,
                    CURRENT_ROUND_KEY to 0L,
                    SINGER_KEY to "",
                    SONG_CHOICES_KEY to ArrayList<String>(),
                    SCORES_KEY to HashMap<String, Int>(),
                    VALIDITY_KEY to true,
                    SONG_CHOICES_LYRICS_KEY to HashMap<String, String>(),
                )
            )
    }

    override fun openGameMetadata(id: Long, usersIds: List<String>): Task<Void> {
        return db.collection(GAMES_METADATA_COLLECTION_PATH).document(id.toString())
            .set(
                hashMapOf(
                    PLAYER_DONE_MAP_KEY to usersIds.associateWith { true },
                    PLAYER_FOUND_MAP_KEY to usersIds.associateWith { false },
                    SCORES_OF_ROUND_KEY to usersIds.associateWith { 0 }
                )
            )
    }

    override fun launchGame(id: Long): Task<Void> {
        val collection = db.collection(LOBBY_COLLECTION_PATH)
        val lobbyRef = collection.document(id.toString())
        val publicRef = collection.document(PUBLIC_DOCUMENT_PATH)
        return db.runTransaction { transaction ->
            val lobbySnapshot = transaction.get(lobbyRef)
            val publicLobbiesSnapshot = transaction.get(publicRef)

            if (!lobbySnapshot.exists()) {
                throw IllegalArgumentException("Document $id not found.")
            }

            if (!publicLobbiesSnapshot.exists()) {
                throw IllegalArgumentException("The public lobbies could not be reached.")
            }

            transaction.update(lobbyRef, LAUNCHED_KEY, true)
            transaction.update(publicRef, "$id", FieldValue.delete())

            // Success
            null
        }
    }

    override fun listenToGameUpdate(id: Long, listener: EventListener<DocumentSnapshot>) {
        removeGameListener()
        gameListerRegistration = listenUpdate(GAMES_COLLECTION_PATH, id, listener)
    }

    override fun removeGameListener(){
        gameListerRegistration?.remove()
        gameListerRegistration = null
    }

    override fun removeLobbyListener(){
        metadataGameListerRegistration?.remove()
        metadataGameListerRegistration = null
    }
    override fun removeMetadataGameListener(){
        lobbyListerRegistration?.remove()
        lobbyListerRegistration = null
    }

    override fun removePublicLobbyListener() {
        publicLobbyListerRegistration?.remove()
        publicLobbyListerRegistration = null
    }

    override fun listenToGameMetadataUpdate(id: Long, listener: EventListener<DocumentSnapshot>) {
        removeMetadataGameListener()
        metadataGameListerRegistration = listenUpdate(GAMES_METADATA_COLLECTION_PATH, id, listener)
    }

    override fun updateGame(id: Long, updatesMap: Map<String, Any>): Task<Void> {
        return db.collection(GAMES_COLLECTION_PATH).document(id.toString())
            .update(updatesMap)
    }

    override fun updateCurrentSongOfGame(id: Long, songName: String, incrementBy: Long): Task<Void> {
        val roundDeadline = Date()
        //incrementBy is in seconds whereas we must add milliseconds to the time before deadline.
        roundDeadline.time += incrementBy*1000
        

        val songUpdateMap = hashMapOf(
            CURRENT_SONG_KEY to songName,
            ROUND_DEADLINE_KEY to Timestamp(roundDeadline)
        )

        return updateGame(id, songUpdateMap)
    }

    override fun playerEndTurn(gameID: Long, playerID: String, hasFound: Boolean): Task<Void> {
        val docRef = db.collection(GAMES_METADATA_COLLECTION_PATH).document(gameID.toString())

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
                PLAYER_DONE_MAP_KEY, updatedDoneMap,
                PLAYER_FOUND_MAP_KEY, updatedFoundMap,
                SCORES_OF_ROUND_KEY, updatedScoreMap
            )

            // Success
            null

        }
    }

    override fun resetGameMetadata(gameID: Long, singer: String): Task<Void> {
        val docRef = db.collection(GAMES_METADATA_COLLECTION_PATH).document(gameID.toString())

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
                PLAYER_DONE_MAP_KEY, updatedDoneMap,
                PLAYER_FOUND_MAP_KEY, updatedFoundMap,
                SCORES_OF_ROUND_KEY, scoresOfRound
            )

            // Success
            null
        }
    }

    private fun listenUpdate(collectionPath : String, id: Long, listener: EventListener<DocumentSnapshot>):ListenerRegistration{
        return db.collection(collectionPath).document(id.toString())
            .addSnapshotListener(listener)
    }




    private fun createListLobbyDataFromRawData(data: Map<String, Any>?): List<LobbyData> {
        return data?.let {
            (it as Map<String, Map<String, String>>).entries.map { (id, data) -> LobbyData(id.toLong(), data[NAME_KEY]!!, data[HOST_KEY]!!) }
        } ?: ArrayList()
    }

}
