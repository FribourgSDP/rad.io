package com.github.fribourgsdp.radio.mockimplementations

import android.content.Context
import android.os.Bundle
import com.github.fribourgsdp.radio.activities.*
import com.github.fribourgsdp.radio.auth.GoogleSignInResult
import com.github.fribourgsdp.radio.data.Genre
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.data.view.UserProfileActivity
import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.utils.*
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import org.mockito.Mockito
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock

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
        val db = mock(Database::class.java)

        val playlist = Playlist(playlistName, Genre.NONE)
        playlist.id = testPlaylistId
        playlist.addSong(Song(testSong1, testArtist))
        playlist.addSong(Song(testSong5, testArtist))
        playlist.addSong(Song(testSong3, testArtist))

        val testUser = User(onlineUserName)
        testUser.id = onlineUserId
        testUser.addPlaylist(playlist)
        testUser.isGoogleUser = true
        Mockito.`when`(db.getUser(anyString())).thenReturn(Tasks.forResult(testUser))
        Mockito.`when`(db.setUser(anyString(), KotlinAny.any())).thenReturn(Tasks.forResult(null))
        return db
    }




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
        user.save(mock(Context::class.java))
        super.onCreate(savedInstanceState)
    }

    fun firebaseAuthWithCredential(credential: AuthCredential, firebaseAuth: FirebaseAuth){
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
        playlist.addSong(Song(testSong1, testArtist))
        playlist.addSong(Song(testSong5, testArtist))
        playlist.addSong(Song(testSong3, testArtist))

        val testUser = User(onlineUserName)
        testUser.id = onlineUserId
        testUser.addPlaylist(playlist)
        testUser.isGoogleUser = true
        Mockito.`when`(db.getUser(anyString())).thenReturn(Tasks.forResult(testUser))
        Mockito.`when`(db.setUser(anyString(),KotlinAny.any())).thenReturn(Tasks.forResult(null))
        return db
    }


}

open class MockUserProfileActivityOffline : UserProfileActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        User.setFSGetter(MockFileSystem.MockFSGetter)
        val user = User(userName, 0)
        user.isGoogleUser = true
        user.id = userId
        val playlist1 = Playlist(playListName, Genre.ROCK)
        playlist1.savedOnline = true
        val song = Song(songName, artistName)
        playlist1.addSong(song)
        user.addPlaylists(setOf(playlist1))
        user.save(mock(Context::class.java))
        super.onCreate(savedInstanceState)
    }

    override fun initializeDatabase(): Database {
        return   mock(Database::class.java)
    }

    override fun hasConnectivity(context: Context): Boolean {
        return false
    }
}



