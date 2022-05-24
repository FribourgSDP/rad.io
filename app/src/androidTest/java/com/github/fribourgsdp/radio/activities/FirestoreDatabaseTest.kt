package com.github.fribourgsdp.radio.activities

import com.github.fribourgsdp.radio.data.Genre
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.database.FirestoreDatabase
import com.github.fribourgsdp.radio.database.FirestoreRef
import com.github.fribourgsdp.radio.database.TransactionManager
import com.github.fribourgsdp.radio.game.Game
import com.github.fribourgsdp.radio.util.getPermissions
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Transaction
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firestore.v1.FirestoreProto
import org.junit.Test
import java.util.concurrent.TimeUnit
import org.junit.Assert.*
import org.junit.Before
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*
import java.io.Serializable

//import java.lang.Exception
class FirestoreDatabaseTest {
    private val userNameTest = "BakerTest"
    private val userIdTest = "ID124Test"

    private val song1 = Song("song1","artist1","lyricsTest1","idTest1")
    private val song2 = Song("song2","artist2","lyricsTest2","idTest2")
    private val song3 = Song("song3","artist3","lyricsTest3","idTest3")
    private val invalidSong = Song("invalidSong","invalidArtist","invalidLyrics","")

    private val playlist1 = Playlist("playlist1", setOf(song1,song2), Genre.FRENCH)
    private val playlist2 = Playlist("playlist2", setOf(song1,song3), Genre.NONE)

    private val playlistListTest = arrayListOf(
        hashMapOf("playlistName" to playlist1.name,"playlistId" to playlist1.id, "genre" to playlist1.genre.toString()),
        hashMapOf("playlistName" to playlist2.name,"playlistId" to playlist2.id, "genre" to playlist2.genre.toString()))

    private lateinit var mockFirestoreRef: FirestoreRef
    private lateinit var mockSnapshot: DocumentSnapshot
    private lateinit var mockTransactionManager: TransactionManager
    private lateinit var mockDocumentReference : DocumentReference
    private lateinit var db : FirestoreDatabase
    @Before
    fun setup(){
        mockFirestoreRef = mock(FirestoreRef::class.java)
        mockSnapshot = mock(DocumentSnapshot::class.java)
        mockDocumentReference = mock(DocumentReference::class.java)
        mockTransactionManager = mock(TransactionManager::class.java)
        db = FirestoreDatabase(mockFirestoreRef, mockTransactionManager)

        //this handling is always the same, what changes is what the document snapshot returns
        `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockSnapshot))
        `when`(mockDocumentReference.set(anyMap<String,String>())).thenReturn( Tasks.whenAll(listOf(Tasks.forResult(3))))

        `when`(mockFirestoreRef.getUserRef((anyString()))).thenReturn(mockDocumentReference)
        `when`(mockFirestoreRef.getSongRef(anyString())).thenReturn(mockDocumentReference)
        `when`(mockFirestoreRef.getPlaylistRef((anyString()))).thenReturn(mockDocumentReference)
        `when`(mockFirestoreRef.getLobbyRef((anyString()))).thenReturn(mockDocumentReference)
        `when`(mockFirestoreRef.getGenericIdRef(anyString(), anyString())).thenReturn(mockDocumentReference)
    }
    @Test
    fun registeringUserAndFetchingItWorks(){

        val userTest = User(userNameTest)

        `when`(mockSnapshot.exists()).thenReturn(true)
        `when`(mockSnapshot.get("username")).thenReturn(userNameTest)
        `when`(mockSnapshot.get("userID")).thenReturn(userIdTest)
        `when`(mockSnapshot.get("playlists")).thenReturn(playlistListTest)
        `when`(mockSnapshot.get("username")).thenReturn(userNameTest)

        db.setUser(userIdTest,userTest)
        val t1 = db.getUser(userIdTest)

        assertEquals(userNameTest,Tasks.await(t1).name)
    }

    @Test
    fun fetchingUnregisteredUserReturnsNull(){
        `when`(mockSnapshot.exists()).thenReturn(false)
        val user = Tasks.withTimeout(db.getUser(userIdTest),10,TimeUnit.SECONDS)
        assertEquals( null,Tasks.await(user))
    }

    @Test
    fun  registerSongAndFetchingItWorks(){
        `when`(mockSnapshot.exists()).thenReturn(true)

        `when`(mockSnapshot.get("songName")).thenReturn("song1")
        `when`(mockSnapshot.get("artistName")).thenReturn("artist1")
        `when`(mockSnapshot.get("lyrics")).thenReturn("lyricsTest1")
        `when`(mockSnapshot.get("songId")).thenReturn("idTest1")

        db.registerSong(song1)
        val song = Tasks.await(db.getSong("idTest1"))
        assertEquals(song1,song)
    }
   

    @Test
    fun registerPlaylistAndFetchingItWorks(){
        db.registerPlaylist(playlist1)

        `when`(mockSnapshot.exists()).thenReturn(true)

        `when`(mockSnapshot.get("playlistName")).thenReturn("playlist1")
        `when`(mockSnapshot.get("genre")).thenReturn("FRENCH")
        `when`(mockSnapshot.get("songs")).thenReturn(arrayListOf(
            hashMapOf("songId" to song1.id,"songName" to song1.name, "songArtist" to song1.artist),
            hashMapOf("songId" to song2.id,"songName" to song2.name, "songArtist" to song2.artist)))

        val t1 = db.getPlaylist("playlist1.id")
        assertEquals(playlist1,Tasks.await(t1))

    }

    @Test
    fun getLobbyIdWorks() {
        `when`(mockSnapshot.exists()).thenReturn(true)
        `when`(mockSnapshot.get("current_id")).thenReturn(3L)
        `when`(mockSnapshot.get("max_id")).thenReturn(1000L)
        `when`(mockTransactionManager.executeIdTransaction(any(), anyString(), anyString(), anyInt())).thenReturn(Tasks.forResult(Pair(3L, 4L)))
        val lobbyId = db.getLobbyId()

        assertEquals(3L, Tasks.await(lobbyId))
    }

    @Test
    fun generateSongIdWorks() {
        `when`(mockSnapshot.exists()).thenReturn(true)
        `when`(mockSnapshot.get("current_id")).thenReturn(3L)
        `when`(mockSnapshot.get("max_id")).thenReturn(1000L)
        `when`(mockTransactionManager.executeIdTransaction(any(), anyString(), anyString(), anyInt())).thenReturn(Tasks.forResult(Pair(3L, 4L)))
        val songId = db.generateSongId()

        assertEquals(3L, Tasks.await(songId))
    }

    @Test
    fun generateUserIdWorks() {
        `when`(mockSnapshot.exists()).thenReturn(true)
        `when`(mockSnapshot.get("current_id")).thenReturn(3L)
        `when`(mockSnapshot.get("max_id")).thenReturn(1000L)
        `when`(mockTransactionManager.executeIdTransaction(any(), anyString(), anyString(), anyInt())).thenReturn(Tasks.forResult(Pair(3L, 4L)))
        val userID = db.generateUserId()

        assertEquals(3L, Tasks.await(userID))
    }

    @Test
    fun generateSongIdsWorks() {
        `when`(mockSnapshot.exists()).thenReturn(true)
        `when`(mockSnapshot.get("current_id")).thenReturn(3L)
        `when`(mockSnapshot.get("max_id")).thenReturn(1000L)
        `when`(mockTransactionManager.executeIdTransaction(any(), anyString(), anyString(), anyInt())).thenReturn(Tasks.forResult(Pair(3L, 6L)))

        val songIds = db.generateSongIds(3)
        assertEquals(Pair(3L, 6L), Tasks.await(songIds))
    }

    @Test
    fun generatePlaylistIdWorks() {
        `when`(mockSnapshot.exists()).thenReturn(true)
        `when`(mockSnapshot.get("current_id")).thenReturn(3L)
        `when`(mockSnapshot.get("max_id")).thenReturn(1000L)
        `when`(mockTransactionManager.executeIdTransaction(any(), anyString(), anyString(), anyInt())).thenReturn(Tasks.forResult(Pair(3L, 4L)))


        val playlistId = db.generatePlaylistId()
        assertEquals(3L, Tasks.await(playlistId))
    }

    @Test(expected = Exception::class)
    fun emptySongNameThrowsExcep() {
        Tasks.await(db.getSong(""))
    }

    @Test(expected = Exception::class)
    fun registerEmptySongNameThrowsExcep() {
        Tasks.await(db.registerSong(Song("", "")))
    }

    @Test(expected = Exception::class)
    fun emptyPlaylistNameThrowsExcep() {
        Tasks.await(db.getPlaylist(""))
    }

    @Test(expected = Exception::class)
    fun registerEmptyPlaylistNameThrowsExcep() {
        Tasks.await(db.registerPlaylist(Playlist("")))
    }

    @Test
    fun openGameWorks() {
        db.openGame(3L)
    }

    @Test
    fun openLobbyWorks() {
        `when`(mockTransactionManager.openLobbyTransaction(any(), any(), anyLong(), any(), any())).thenReturn(Tasks.forResult(null))
        var mockDocumentReference2 = mock(DocumentReference::class.java)
        `when`(mockFirestoreRef.getPublicLobbiesRef()).thenReturn(mockDocumentReference2)


        `when`(mockSnapshot.exists()).thenReturn(true)
        val fakeSettings = Game.Settings("host22", "gameName", "playlistBla", 3, false, true)

        db.openLobby(3L, fakeSettings)
    }

    @Test
    fun modifyingPermissionsWorks(){
        `when`(mockSnapshot.exists()).thenReturn(true)
        val mockPermissions = hashMapOf(Pair("user1", true), Pair("user2", false))
        `when`(mockSnapshot.get("permissions")).thenReturn(mockPermissions)
        `when`(mockTransactionManager.executeMicPermissionsTransaction(any(), anyString(), anyBoolean(), anyLong())).thenReturn(Tasks.forResult(null))
        val result = db.modifyUserMicPermissions(37, User("nate"), true)
        assertEquals(null, Tasks.await(result))
    }

    @Test
    fun testMicPermissionsOnTransactionMgr() {
        val fireDb = FirestoreDatabase()
        var user = User("nate")
        user.id = "testUser1"
        fireDb.modifyUserMicPermissions(123321, user, true)
        fireDb.refMake.getLobbyRef("123321").get().addOnCompleteListener { snapshot ->
            println("In here")
            val newPermissions = snapshot.result.getPermissions()
            assertEquals(true, newPermissions["testUser1"])
        }
    }

    @Test
    fun getGameSettingsFromLobbyWorks() {
        val fireDb = FirestoreDatabase()
        var expected = Game.Settings("testHost", "A test lobby for testing", "testPlaylist", 3, false, true)
        val result = fireDb.getGameSettingsFromLobby(123321)
        assertEquals(expected, Tasks.await(result))
    }

    @Test
    fun removeUserFromLobbyWorks() {
        val fireDb = FirestoreDatabase()
        var user = User("nate2")
        user.id = "testUser2"
        val result = fireDb.removeUserFromLobby(123321, user)
        assertEquals(null, Tasks.await(result))
        //Revert changes
        fireDb.addUserToLobby(123321, user, false)
    }

    @Test
    fun disableGameWorks() {
        val fireDb = FirestoreDatabase()
        val gameId = 123321L
        val result = fireDb.disableGame(gameId)
        assertEquals(null, Tasks.await(result))
        //Revert changes
        fireDb.transactionMgr.db.runTransaction {
            val collection = fireDb.transactionMgr.db.collection("games")
            val gameRef = collection.document(gameId.toString())
            assertEquals(false, it.get(gameRef)["validity"] as Boolean)
            it.update(gameRef, "validity", true)
            null
        }
    }

    @Test
    fun disableLobbyWorks() {
        val fireDb = FirestoreDatabase()
        val gameId = 123321L
        val result = fireDb.disableLobby(gameId)
        assertEquals(null, Tasks.await(result))
        //Revert changes
        fireDb.transactionMgr.db.runTransaction {
            val collection = fireDb.transactionMgr.db.collection("lobby")
            val lobbyRef = collection.document(gameId.toString())
            assertEquals(false, it.get(lobbyRef)["validity"] as Boolean)
            it.update(lobbyRef, "validity", true)
            null
        }
    }

    @Test
    fun removePlayerFromGameWorks() {
        val fireDb = FirestoreDatabase()
        var user = User("nate2")
        user.id = "testUser2"
        val gameId = 123321L
        val result = fireDb.removePlayerFromGame(gameId, user)
        assertEquals(null, Tasks.await(result))
        //Revert changes
        fireDb.transactionMgr.db.runTransaction {
            val collection = fireDb.transactionMgr.db.collection("games_metadata")
            val gameMetaRef = collection.document(gameId.toString())
            val snapshot = it.get(gameMetaRef)
            val playerDoneMap = snapshot.get("player_done_map")!! as HashMap<String, Boolean>
            val playerFoundMap = snapshot.get("player_found_map")!! as HashMap<String, Boolean>
            assertEquals(false, playerDoneMap.containsKey("testUser2"))
            assertEquals(false, playerFoundMap.containsKey("testUser2"))
            playerDoneMap.put("testUser2", true)
            playerFoundMap.put("testUser2", true)
            it.update(gameMetaRef, "player_done_map", playerDoneMap)
            it.update(gameMetaRef, "player_found_map", playerFoundMap)
            null
        }
    }

    @Test
    fun openGameMetadataWorks() {
        val fireDb = FirestoreDatabase()
        val users = listOf("testUser1", "testUser2")
        val gameId = 123321L
        val result = fireDb.openGameMetadata(gameId, users)
        assertEquals(null, Tasks.await(result))
        fireDb.transactionMgr.db.runTransaction {
            val collection = fireDb.transactionMgr.db.collection("games_metadata")
            val gameMetaRef = collection.document(gameId.toString())
            val snapshot = it.get(gameMetaRef)
            assertEquals(users.size, (snapshot.get("player_done_map") as HashMap<String, Boolean>).size)
            assertEquals(users.size, (snapshot.get("player_found_map") as HashMap<String, Boolean>).size)
            assertEquals(users.size, (snapshot.get("scores_of_round") as HashMap<String, Boolean>).size)
            null
        }
    }

    @Test
    fun launchGameWorks() {
        val fireDb = FirestoreDatabase()
        val gameId = 123321L
        val result = fireDb.launchGame(gameId)
        assertEquals(null, Tasks.await(result))
        //Revert changes
        fireDb.transactionMgr.db.runTransaction {
            val collection = fireDb.transactionMgr.db.collection("lobby")
            val lobbyRef = collection.document(gameId.toString())
            val snapshot = it.get(lobbyRef)
            assertEquals(true, snapshot.get("launched"))
            it.update(lobbyRef, "launched", false)
            null
        }
    }

    @Test
    fun makeSingerDoneWorks() {
        val fireDb = FirestoreDatabase()
        var user = User("nate2")
        user.id = "testUser2"
        val gameId = 123321L
        val result = fireDb.makeSingerDone(gameId, user.id)
        assertEquals(null, Tasks.await(result))
        //Revert changes
        fireDb.transactionMgr.db.runTransaction {
            val collection = fireDb.transactionMgr.db.collection("games_metadata")
            val gameRef = collection.document(gameId.toString())
            val snapshot = it.get(gameRef)
            val playerDoneMap = snapshot.get("player_done_map")!! as HashMap<String, Boolean>
            assertEquals(true, playerDoneMap["testUser2"])
            playerDoneMap["testUser2"] = false
            it.update(gameRef, "player_done_map", playerDoneMap)
            null
        }
    }
}