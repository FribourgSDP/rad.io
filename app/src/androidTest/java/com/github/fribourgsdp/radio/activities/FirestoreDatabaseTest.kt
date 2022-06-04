package com.github.fribourgsdp.radio.activities

import com.github.fribourgsdp.radio.data.*
import com.github.fribourgsdp.radio.database.FirestoreDatabase
import com.github.fribourgsdp.radio.database.FirestoreRef
import com.github.fribourgsdp.radio.database.TransactionManager
import com.github.fribourgsdp.radio.database.SONG_CHOICES_KEY
import com.github.fribourgsdp.radio.game.Game
import com.github.fribourgsdp.radio.util.*
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import org.junit.Test
import java.util.concurrent.TimeUnit
import org.junit.Assert.*
import org.junit.Before
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*

//import java.lang.Exception
class FirestoreDatabaseTest {
    private val userNameTest = "BakerTest"
    private val userIdTest = "ID124Test"
    private val songTest = "fakeSongDoesNotExist"
    private val playlistTest = "fakePlaylistDoesNotExist"
    private val fireDb = FirestoreDatabase()
    private var user1 = User("nate")
    private var user2 = User("nate2")
    private var user1ID = "testUser1"
    private var user2ID = "testUser2"
    private val unusedId = 987987L
    private val testingLobbyId = 123321L
    private val unusedLobbyForPublic = 123456789L
    private val openGameTestId = 6666L
    private val idForOpenLobby = 6667L
    private val unusedUserId = "thisUserIdIsSuperLongAndHasNoChanceOfBeingUsed"

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
        user1.id = user1ID
        user2.id = user2ID

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
    fun registerSongAndFetchingItWorks(){
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
    fun getUnregisteredSongReturnsNull() {
        `when`(mockSnapshot.exists()).thenReturn(false)
        val song = Tasks.withTimeout(db.getSong(songTest),10,TimeUnit.SECONDS)
        assertEquals( null,Tasks.await(song))
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

    @Test(expected = Exception::class)
    fun registerPlaylistWithSongWithNoNameThrowsException() {
        val fakePlaylist = Playlist("fake")
        fakePlaylist.addSong(Song("", ""))
        Tasks.await(fireDb.registerPlaylist(fakePlaylist))
    }

    @Test
    fun getUnregisteredPlaylistReturnsNull() {
        `when`(mockSnapshot.exists()).thenReturn(false)
        val playlist = Tasks.withTimeout(db.getPlaylist(playlistTest),10,TimeUnit.SECONDS)
        assertEquals( null,Tasks.await(playlist))
    }

    @Test(expected = Exception::class)
    fun getNonExistingGameSettingsFails() {
        Tasks.await(fireDb.getGameSettingsFromLobby(3333))
    }

    @Test(expected = Exception::class)
    fun addingUserToNonExistingLobbyFails() {
        val user = User("unusedUser")
        user.id = unusedId.toString()
        Tasks.await(fireDb.addUserToLobby(unusedId,user, true))
    }

    @Test(expected = Exception::class)
    fun addingUserAlreadyInLobbyFails() {
        val user = user1
        Tasks.await(fireDb.addUserToLobby(testingLobbyId ,user, true))
    }

    @Test(expected = Exception::class)
    fun removingUserNotInLobbyFails() {
        val user = User("nate3")
        user.id = "testUser3"
        Tasks.await(fireDb.removeUserFromLobby(testingLobbyId ,user))
    }

    @Test(expected = Exception::class)
    fun disablingGameFailsIfWrongId() {
        Tasks.await(fireDb.disableGame(unusedId))
    }

    @Test(expected = Exception::class)
    fun disablingLobbyFailsIfWrongId() {
        Tasks.await(fireDb.disableLobby(unusedId))
    }

    @Test(expected = Exception::class)
    fun makeSingerDoneFailsIfWrongId() {
        Tasks.await(fireDb.makeSingerDone(unusedId, unusedUserId))
    }

    @Test(expected = Exception::class)
    fun launchGameFailsIfWrongId() {
        Tasks.await(fireDb.launchGame(unusedId))
    }

    @Test(expected = Exception::class)
    fun playerEndTurnFailsIfWrongId() {
        Tasks.await(fireDb.playerEndTurn(unusedId, unusedUserId, false))
    }

    @Test(expected = Exception::class)
    fun resetGameMetadataFailsIfWrongId() {
        Tasks.await(fireDb.resetGameMetadata(unusedId, unusedUserId))
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
        Tasks.await(db.openGame(openGameTestId))
        fireDb.transactionMgr.db.runTransaction {
            val collection = fireDb.transactionMgr.db.collection("games")
            val gameMetaRef = collection.document(openGameTestId.toString())
            val snapshot = it.get(gameMetaRef)
            assertEquals(false, snapshot.get("finished") as Boolean)
            assertEquals(0L, snapshot.get("current_round") as Long)
            assertEquals("", snapshot.get("singer") as String)
            assertEquals(ArrayList<String>(), snapshot.getAndCast<ArrayList<String>>(SONG_CHOICES_KEY))
            assertEquals(HashMap<String, Int>(), snapshot.getAndCast<HashMap<String, Int>>("scores"))
            assertEquals(true, snapshot.get("validity") as Boolean)
            assertEquals(HashMap<String, String>(), snapshot.getAndCast<HashMap<String, String>>("song_choices_lyrics"))
            null
        }
    }

    @Test
    fun openLobbyWorks() {
        `when`(mockTransactionManager.openLobbyTransaction(any(), any(), anyLong(), any(), any())).thenReturn(Tasks.forResult(null))
        val mockDocumentReference2 = mock(DocumentReference::class.java)
        `when`(mockFirestoreRef.getPublicLobbiesRef()).thenReturn(mockDocumentReference2)


        `when`(mockSnapshot.exists()).thenReturn(true)
        val fakeSettings = Game.Settings("host22", "gameName", "playlistBla", 3, false, true, 45)

        val result = db.openLobby(idForOpenLobby, fakeSettings)
        assertEquals(null, Tasks.await(result))
    }

    @Test
    fun modifyingPermissionsWorks(){
        `when`(mockSnapshot.exists()).thenReturn(true)
        val mockPermissions = hashMapOf(Pair("user1", true), Pair("user2", false))
        `when`(mockSnapshot.get("permissions")).thenReturn(mockPermissions)
        `when`(mockTransactionManager.executeMicPermissionsTransaction(any(), anyString(), anyBoolean(), anyLong())).thenReturn(Tasks.forResult(null))
        val result = db.modifyUserMicPermissions(unusedId, User("nate"), true)
        assertEquals(null, Tasks.await(result))
    }

    @Test
    fun testMicPermissionsOnTransactionMgr() {
        val user = user1
        fireDb.modifyUserMicPermissions(testingLobbyId, user, true)
        fireDb.refMake.getLobbyRef(testingLobbyId.toString()).get().addOnCompleteListener { snapshot ->
            val newPermissions = snapshot.result.getPermissions()
            assertEquals(true, newPermissions[user1ID])
        }
    }

    @Test
    fun getGameSettingsFromLobbyWorks() {
        val expected = Game.Settings("testHost", "A test lobby for testing", "testPlaylist", 3, false, true, 30L)
        val result = fireDb.getGameSettingsFromLobby(testingLobbyId)
        assertEquals(expected, Tasks.await(result))
    }

    @Test
    fun removeUserFromLobbyWorks() {
        val user = user2
        val result = fireDb.removeUserFromLobby(testingLobbyId, user)
        assertEquals(null, Tasks.await(result))
        //Revert changes
        fireDb.addUserToLobby(testingLobbyId, user, false)
    }
    @Test
    fun disableGameWorks() {
        val gameId = testingLobbyId
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
        val gameId = testingLobbyId
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
        val gameId = testingLobbyId
        val result = fireDb.removePlayerFromGame(gameId, user2)
        assertEquals(null, Tasks.await(result))
        //Revert changes
        fireDb.transactionMgr.db.runTransaction {
            val collection = fireDb.transactionMgr.db.collection("games_metadata")
            val gameMetaRef = collection.document(gameId.toString())
            val snapshot = it.get(gameMetaRef)
            val playerDoneMap = snapshot.getAndCast<HashMap<String, Boolean>>("player_done_map")
            val playerFoundMap = snapshot.getAndCast<HashMap<String, Boolean>>("player_found_map")
            assertEquals(false, playerDoneMap.containsKey(user2ID))
            assertEquals(false, playerFoundMap.containsKey(user2ID))
            playerDoneMap[user2ID] = true
            playerFoundMap[user2ID] = true
            it.update(gameMetaRef, "player_done_map", playerDoneMap)
            it.update(gameMetaRef, "player_found_map", playerFoundMap)
            null
        }
    }

    @Test
    fun openGameMetadataWorks() {
        val users = listOf(user1ID, user2ID)
        val gameId = testingLobbyId
        val result = fireDb.openGameMetadata(gameId, users)
        assertEquals(null, Tasks.await(result))
        fireDb.transactionMgr.db.runTransaction {
            val collection = fireDb.transactionMgr.db.collection("games_metadata")
            val gameMetaRef = collection.document(gameId.toString())
            val snapshot = it.get(gameMetaRef)
            assertEquals(users.size, (snapshot.getAndCast<HashMap<String, Boolean>>("player_done_map")).size)
            assertEquals(users.size, (snapshot.getAndCast<HashMap<String, Boolean>>("player_found_map")).size)
            assertEquals(users.size, (snapshot.getAndCast<HashMap<String, Boolean>>("scores_of_round")).size)
            null
        }
    }

    @Test
    fun launchGameWorks() {
        val gameId = testingLobbyId
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
        val user = user2
        val gameId = testingLobbyId
        val result = fireDb.makeSingerDone(gameId, user.id)
        assertEquals(null, Tasks.await(result))
        //Revert changes
        fireDb.transactionMgr.db.runTransaction {
            val collection = fireDb.transactionMgr.db.collection("games_metadata")
            val gameRef = collection.document(gameId.toString())
            val snapshot = it.get(gameRef)
            val playerDoneMap = snapshot.getAndCast<HashMap<String, Boolean>>("player_done_map")
            assertEquals(true, playerDoneMap[user2ID])
            playerDoneMap[user2ID] = false
            it.update(gameRef, "player_done_map", playerDoneMap)
            null
        }
    }

    @Test
    fun playerEndTurnWorksHasFound() {
        val user = user2
        val gameId = testingLobbyId
        val result = fireDb.playerEndTurn(gameId, user.id, true)
        assertEquals(null, Tasks.await(result))
        fireDb.transactionMgr.db.runTransaction {
            val collection = fireDb.transactionMgr.db.collection("games_metadata")
            val gameRef = collection.document(gameId.toString())
            val snapshot = it.get(gameRef)
            assertNotEquals(0, (snapshot.getAndCast<HashMap<String, Int>>("scores_of_round"))[user2ID])
            null
        }
        fireDb.resetGameMetadata(gameId, user1ID)
    }

    @Test
    fun playerEndTurnWorksHasNotFound() {
        val user = user2
        val gameId = testingLobbyId
        val result = fireDb.playerEndTurn(gameId, user.id, false)
        assertEquals(null, Tasks.await(result))
        fireDb.transactionMgr.db.runTransaction {
            val collection = fireDb.transactionMgr.db.collection("games_metadata")
            val gameRef = collection.document(gameId.toString())
            val snapshot = it.get(gameRef)
            assertEquals(0, (snapshot.getAndCast<HashMap<String, Int>>("scores_of_round"))[user2ID])
            null
        }
        fireDb.resetGameMetadata(gameId, user1ID)
    }

    @Test
    fun resetGameMetadataWorks() {
        val user = user2
        val gameId = testingLobbyId
        val result = fireDb.resetGameMetadata(gameId, user.id)
        assertEquals(null, Tasks.await(result))
        fireDb.transactionMgr.db.runTransaction {
            val collection = fireDb.transactionMgr.db.collection("games_metadata")
            val gameRef = collection.document(gameId.toString())
            val snapshot = it.get(gameRef)
            val updatedDoneMap = snapshot.getPlayerDoneMap()
            for ((id, done) in updatedDoneMap) {
                assertEquals(id == user.id, done)
            }
            val updatedFoundMap = snapshot.getPlayerFoundMap()
            for ((_, found) in updatedFoundMap) {
                assertEquals(false, found)
            }
            val scoresOfRound = snapshot.getScoresOfRound<Long>()
            for ((_, score) in scoresOfRound) {
                assertEquals(0, score)
            }
            assertEquals(0, (snapshot.getAndCast<HashMap<String, Int>>("scores_of_round"))[user2ID])
            null
        }
    }

    @Test
    fun updateCurrentSongOfGameWorks() {
        val gameId = testingLobbyId
        val newSongName = "songiSonga"
        val result = fireDb.updateCurrentSongOfGame(gameId, newSongName, 5)
        assertEquals(null, Tasks.await(result))
        fireDb.transactionMgr.db.runTransaction {
            val collection = fireDb.transactionMgr.db.collection("games")
            val gameRef = collection.document(gameId.toString())
            val snapshot = it.get(gameRef)
            assertNotEquals(null, snapshot.get("round_deadline"))
            assertEquals(newSongName, snapshot.get("current_song"))
            null
        }
    }

    @Test
    fun addGetAndRemovePublicLobbiesWorks() {
        val lobbyId = unusedLobbyForPublic
        val fakeSettings = Game.Settings("host22", "lobbyName", "playlistBla", 3,
            withHint = false,
            isPrivate = false,
            singerDuration = 45
        )
        Tasks.await(fireDb.openLobby(lobbyId, fakeSettings))
        val publicLobbies = Tasks.await(fireDb.getPublicLobbies())
        assertEquals(true, publicLobbies.contains(LobbyData(lobbyId, "lobbyName", "host22")))
        Tasks.await(fireDb.removeLobbyFromPublic(lobbyId))
        val newPublicLobbies = Tasks.await(fireDb.getPublicLobbies())
        assertEquals(false, newPublicLobbies.contains(LobbyData(lobbyId, "lobbyName", "host22")))
    }
}