package com.github.fribourgsdp.radio.mockimplementations

import android.content.Context
import android.os.Bundle
import com.github.fribourgsdp.radio.activities.makeMockAdditionalUserInfo
import com.github.fribourgsdp.radio.activities.makeMockAuthResult
import com.github.fribourgsdp.radio.activities.makeMockFireBaseAuth
import com.github.fribourgsdp.radio.activities.makeMockFirebaseUser
import com.github.fribourgsdp.radio.auth.GoogleSignInResult
import com.github.fribourgsdp.radio.data.Genre
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.data.view.UserProfileActivity
import com.github.fribourgsdp.radio.database.Database
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import org.mockito.Mockito
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock

const val userName = "test"
const val playListName = "testTitle"
const val songName = "TestSongName"
const val userId = "testId"
const val artistName = "artist"
const val playlistName = "testPlaylist"
const val testPlaylistId = "TEST_PLAYLIST"
const val onlineUserId = "onlineUserTestId"
const val onlineUserName = "onlineUserTest"

class GoogleUserMockUserProfileActivity : UserProfileActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        User.setFSGetter(MockFileSystem.MockFSGetter)
        val user = User(userName, 0)
        user.isGoogleUser = true
        user.id = userId
        val playlist1 = Playlist(playListName, Genre.ROCK)
        val song = Song(songName, artistName)
        playlist1.addSong(song)
        user.addPlaylists(setOf(playlist1))
        user.save(mock(Context::class.java))
        super.onCreate(savedInstanceState)
    }


    override fun initializeDatabase(): Database {
        val db = Mockito.mock(Database::class.java)

        val playlist : Playlist = Playlist(playlistName, Genre.NONE)
        playlist.id = testPlaylistId
        playlist.addSong(Song("rouge", "sardou"))
        playlist.addSong(Song("salut", "sardou"))
        playlist.addSong(Song("Le France", "sardou"))

        val testUser = User(onlineUserName)
        testUser.id = onlineUserId
        testUser.addPlaylist(playlist)
        testUser.isGoogleUser = true
        Mockito.`when`(db.getUser(anyString())).thenReturn(Tasks.forResult(testUser))
        Mockito.`when`(db.setUser(anyString(),any())).thenReturn(Tasks.forResult(null))
        return db
    }

    //this is usefull in order to be able to use any() from mockito
    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }
    private fun <T> uninitialized(): T = null as T


}


class GoogleSignInTestMockUserProfileActivity : MockUserProfileActivity()  {

    var normalLogin = false
    var newLogin = false
    var failLogin = false
    var alreadyLogin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        User.setFSGetter(MockFileSystem.MockFSGetter)
        val user = User(userName, 0)
        user.id = userId
        val playlist1 = Playlist(playListName, Genre.ROCK)
        val song = Song(songName, artistName)
        playlist1.addSong(song)
        user.addPlaylists(setOf(playlist1))
        user.save(Mockito.mock(Context::class.java))
        super.onCreate(savedInstanceState)
    }

    fun firebaseAuthWithCredentitial(credential: AuthCredential, firebaseAuth: FirebaseAuth){
        googleSignIn.firebaseAuthWithCredentitial(credential, firebaseAuth)
    }

    override fun loginFromGoogle(code : GoogleSignInResult){

        when (code) {
            GoogleSignInResult.FAIL -> failLogin = true
            GoogleSignInResult.NEW_USER -> newLogin = true
            GoogleSignInResult.NORMAL_USER -> normalLogin = true
            GoogleSignInResult.ALREADY_LOGIN -> alreadyLogin = true
        }
    }

    override fun signInOrOut(){
        val mockAdditionalUserInfo = makeMockAdditionalUserInfo(false)
        val mockUser = makeMockFirebaseUser()
        val mockAuthResult = makeMockAuthResult(mockAdditionalUserInfo,mockUser)
        val mockFireBaseAuth = makeMockFireBaseAuth(false, mockAuthResult,mockUser)

        googleSignIn.signIn(mockFireBaseAuth)
    }
}

open class MockUserProfileActivity : UserProfileActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        User.setFSGetter(MockFileSystem.MockFSGetter)
        val user = User(userName, 0)
        user.id = userId
        val playlist1 = Playlist(playListName, Genre.ROCK)
        val song = Song(songName, artistName)
        playlist1.addSong(song)
        user.addPlaylists(setOf(playlist1))
        user.save(mock(Context::class.java))
        super.onCreate(savedInstanceState)
    }

    override fun initializeDatabase(): Database {
        val db = mock(Database::class.java)
        val playlist = Playlist(playlistName, Genre.NONE)
        playlist.id = testPlaylistId
        playlist.addSong(Song("rouge", "sardou"))
        playlist.addSong(Song("salut", "sardou"))
        playlist.addSong(Song("Le France", "sardou"))

        val testUser = User(onlineUserName)
        testUser.id = onlineUserId
        testUser.addPlaylist(playlist)
        testUser.isGoogleUser = true
        Mockito.`when`(db.getUser(anyString())).thenReturn(Tasks.forResult(testUser))
        Mockito.`when`(db.setUser(anyString(),any())).thenReturn(Tasks.forResult(null))
        return db
    }

    //this is usefull in order to be able to use any() from mockito
    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }
    private fun <T> uninitialized(): T = null as T


}


