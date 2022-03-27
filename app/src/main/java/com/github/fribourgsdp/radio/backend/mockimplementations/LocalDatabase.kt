package com.github.fribourgsdp.radio.backend.mockimplementations

import com.github.fribourgsdp.radio.backend.database.Database
import com.github.fribourgsdp.radio.backend.gameplay.Game
import com.github.fribourgsdp.radio.backend.gameplay.User
import com.github.fribourgsdp.radio.backend.music.Playlist
import com.github.fribourgsdp.radio.backend.music.Song
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener

class LocalDatabase : Database {
    private val userMap: MutableMap<String, User> = mutableMapOf()
    private val songMap: MutableMap<String, Song> = mutableMapOf()
    private val playlistMap: MutableMap<String, Playlist> = mutableMapOf()


    override fun getUser(userId: String): Task<User> {
        return Tasks.forResult(userMap[userId])
    }

    override fun setUser(userId: String, user: User): Task<Void> {
        userMap[userId] = User(user.name)
        return Tasks.forResult(null)
    }

    override fun getSong(songName: String): Task<Song> {
        return Tasks.forResult(songMap[songName])
    }

    override fun registerSong(song: Song): Task<Void> {
        songMap[song.name] = song
        return Tasks.forResult(null)
    }

    override fun getPlaylist(playlistName: String): Task<Playlist> {
        return Tasks.forResult(playlistMap[playlistName])
    }

    override fun registerPlaylist(playlist: Playlist): Task<Void> {
        val titleList : MutableList<Song> = mutableListOf()
        for( song in playlist.getSongs()){
            titleList.add(Song(song.name,song.artist,""))
        }
        playlistMap[playlist.name] = Playlist(playlist.name,titleList.toSet(),playlist.genre)

        return Tasks.forResult(null)
    }

    override fun getLobbyId(): Task<Long> {
        return Tasks.forResult(EXPECTED_UID)
    }

    override fun openLobby(id: Long, settings: Game.Settings): Task<Void> {
        return Tasks.forResult(null)
    }

    override fun listenToLobbyUpdate(id: Long, listener: EventListener<DocumentSnapshot>) {
        return
    }

    override fun getGameSettingsFromLobby(id: Long): Task<Game.Settings> {
        return Tasks.forResult(EXPECTED_SETTINGS)
    }

    override fun addUserToLobby(id: Long, user: User): Task<Void> {
        return Tasks.forResult(null)
    }

    companion object {
        const val EXPECTED_UID = 794L
        val EXPECTED_SETTINGS = Game.Settings(User("Host"), "Hello World!", Playlist("Host's Playlist"), 42, true, true)

    }
}