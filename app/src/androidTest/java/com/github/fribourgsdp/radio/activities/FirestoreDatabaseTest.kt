package com.github.fribourgsdp.radio.activities

import com.github.fribourgsdp.radio.data.Genre
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.database.FirestoreDatabase
import com.github.fribourgsdp.radio.database.FirestoreRef
import com.github.fribourgsdp.radio.database.TransactionManager
import com.github.fribourgsdp.radio.game.Game
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import org.junit.Test
import java.util.concurrent.TimeUnit
import org.junit.Assert.*
import org.junit.Before
import org.mockito.ArgumentMatcher
import org.mockito.ArgumentMatchers
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*
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
        `when`(mockFirestoreRef.getGameRef((anyString()))).thenReturn(mockDocumentReference)
        `when`(mockFirestoreRef.getGameMetadataRef((anyString()))).thenReturn(mockDocumentReference)
        `when`(mockFirestoreRef.getPlaylistInfoMetadataRef()).thenReturn(mockDocumentReference)
        `when`(mockFirestoreRef.getSongInfoMetadataRef()).thenReturn(mockDocumentReference)
        `when`(mockFirestoreRef.getUserInfoMetadataRef()).thenReturn(mockDocumentReference)
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
    fun modifyingPermissionsWorks(){
        `when`(mockSnapshot.exists()).thenReturn(true)
        val mockPermissions = hashMapOf(Pair("user1", true), Pair("user2", false))
        `when`(mockSnapshot.get("permissions")).thenReturn(mockPermissions)
        `when`(mockTransactionManager.executeMicPermissionsTransaction(any(), anyString(), anyBoolean(), anyLong())).thenReturn(Tasks.forResult(null))
        val result = db.modifyUserMicPermissions(37, User("nate"), true)
        assertEquals(null, Tasks.await(result))
    }

}