package com.github.fribourgsdp.radio.backend.mockimplementations

import com.github.fribourgsdp.radio.backend.*
import com.github.fribourgsdp.radio.backend.database.Database
import com.github.fribourgsdp.radio.backend.gameplay.Game
import com.github.fribourgsdp.radio.backend.gameplay.User
import com.github.fribourgsdp.radio.backend.music.Playlist
import com.github.fribourgsdp.radio.backend.music.Song
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener

class BuggyDatabase : Database {
    //TODO("use the exception that will be created")
    override fun getUser(userId: String): Task<User> {
        return Tasks.forException(Exception("Error"))
    }

    override fun setUser(userId: String, user: User): Task<Void> {
        return Tasks.forException(Exception("Error"))
    }

    override fun getSong(songName: String): Task<Song> {
        return Tasks.forException(Exception("Error"))
    }

    override fun registerSong(song: Song): Task<Void> {
        return Tasks.forException(Exception("Error"))
    }

    override fun getPlaylist(playlistName: String): Task<Playlist> {
        return Tasks.forException(Exception("Error"))
    }

    override fun registerPlaylist(playlist: Playlist): Task<Void> {
        return Tasks.forException(Exception("Error"))
    }

    override fun getLobbyId(): Task<Long> {
        return Tasks.forException(Exception("Error"))
    }

    override fun openLobby(id: Long, settings: Game.Settings): Task<Void> {
        return Tasks.forException(Exception("Error"))
    }

    override fun listenToLobbyUpdate(id: Long, listener: EventListener<DocumentSnapshot>) {

    }

    override fun getGameSettingsFromLobby(id: Long): Task<Game.Settings> {
        return Tasks.forException(Exception("Error"))
    }

    override fun addUserToLobby(id: Long, user: User): Task<Void> {
        return Tasks.forException(Exception("Error"))
    }
}