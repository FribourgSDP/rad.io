package com.github.fribourgsdp.radio

import android.content.ContentValues
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import org.junit.Test
import java.util.concurrent.TimeUnit
import org.junit.Assert.*
import org.junit.Before
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*
import java.util.*
import kotlin.collections.HashMap

class FirestoreDatabaseTest {
    private val userNameTest = "BakerTest"
    private val userIdTest = "ID124Test"

    private val song1 = Song("song1","artist1","lyricsTest1","idTest1")
    private val song2 = Song("song2","artist2","lyricsTest2","idTest2")
    private val song3 = Song("song3","artist3","lyricsTest3","idTest3")
    private val invalidSong = Song("invalidSong","invalidArtist","invalidLyrics","")

    private val playlist1 = Playlist("playlist1", setOf(song1,song2),Genre.FRENCH)
    private val playlist2 = Playlist("playlist2", setOf(song1,song3),Genre.NONE)

    private val playlistListTest = arrayListOf(
        hashMapOf("playlistName" to playlist1.name,"playlistId" to playlist1.id, "genre" to playlist1.genre.toString()),
        hashMapOf("playlistName" to playlist2.name,"playlistId" to playlist2.id, "genre" to playlist2.genre.toString()))

    private lateinit var mockFirestoreRef: FirestoreRef
    private lateinit var mockSnapshot: DocumentSnapshot
    private lateinit var mockDocumentReference : DocumentReference
    private lateinit var db : FirestoreDatabase
    @Before
    fun setup(){


        mockFirestoreRef = mock(FirestoreRef::class.java)
        mockSnapshot = mock(DocumentSnapshot::class.java)
        mockDocumentReference = mock(DocumentReference::class.java)
        db = FirestoreDatabase(mockFirestoreRef)

        //this handling is always the same, what changes is what the document snapshot returns
        `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockSnapshot))
        `when`(mockDocumentReference.set(anyMap<String,String>())).thenReturn( Tasks.whenAll(listOf(Tasks.forResult(3))))

        `when`(mockFirestoreRef.getUserRef((anyString()))).thenReturn(mockDocumentReference)
        `when`(mockFirestoreRef.getSongRef(anyString())).thenReturn(mockDocumentReference)
        `when`(mockFirestoreRef.getPlaylistRef((anyString()))).thenReturn(mockDocumentReference)

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
   /* @Test(expected = IllegalArgumentException::class)
    fun registerSongWithNoIdThrowsException(){
        Tasks.await( db.registerSong(invalidSong))
        /*val t1 = db.registerSong(invalidSong)
        assertEquals(IllegalArgumentException("Not null id is expected"),
        Tasks.await(t1)
        )*/
    }*/

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
}