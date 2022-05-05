package com.github.fribourgsdp.radio.mockimplementations

import com.github.fribourgsdp.radio.*
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.data.User
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

    override fun generateSongIds(number: Int): Task<Pair<Long, Long>> {
        return Tasks.forException(Exception("Error"))
    }

    override fun generateUserId(): Task<Long> {
        return Tasks.forException(Exception("Error"))
    }

    override fun generatePlaylistId(): Task<Long> {
        TODO("Not yet implemented")
    }

    override fun generateSongId(): Task<Long> {
        TODO("Not yet implemented")
    }

    override fun openLobby(id: Long, settings: Game.Settings): Task<Void> {
        return Tasks.forException(Exception("Error"))
    }

    override fun listenToLobbyUpdate(id: Long, listener: EventListener<DocumentSnapshot>) {

    }

    override fun getGameSettingsFromLobby(id: Long): Task<Game.Settings> {
        return Tasks.forException(Exception("Error"))
    }

    override fun modifyUserMicPermissions(
        id: Long,
        user: User,
        newPermissions: Boolean
    ): Task<Void> {
        TODO("Not yet implemented")
    }

    override fun addUserToLobby(id: Long, user: User, hasMicPermissions: Boolean): Task<Void> {
        return Tasks.forException(Exception("Error"))
    }

    override fun openGame(id: Long): Task<Void> {
        TODO("Not yet implemented")
    }

    override fun openGameMetadata(id: Long, usersIds: List<String>): Task<Void> {
        TODO("Not yet implemented")
    }

    override fun launchGame(id: Long): Task<Void> {
        TODO("Not yet implemented")
    }

    override fun listenToGameUpdate(id: Long, listener: EventListener<DocumentSnapshot>) {
        TODO("Not yet implemented")
    }

    override fun listenToGameMetadataUpdate(id: Long, listener: EventListener<DocumentSnapshot>) {
        TODO("Not yet implemented")
    }

    override fun updateGame(id: Long, updatesMap: Map<String, Any>): Task<Void> {
        TODO("Not yet implemented")
    }

    override fun playerEndTurn(gameID: Long, playerID: String, hasFound: Boolean): Task<Void> {
        TODO("Not yet implemented")
    }

    override fun resetGameMetadata(gameID: Long, singer: String): Task<Void> {
        TODO("Not yet implemented")
    }

    override fun removeUserFromLobby(id: Long, user: User): Task<Void> {
        TODO("Not yet implemented")
    }

    override fun disableGame(id: Long): Task<Void> {
        TODO("Not yet implemented")
    }

    override fun disableLobby(gameID: Long): Task<Void> {
        TODO("Not yet implemented")
    }

    override fun removePlayerFromGame(gameID: Long, user: User): Task<Void> {
        TODO("Not yet implemented")
    }

    override fun makeSingerDone(gameID: Long, singerName: String): Task<Void> {
        TODO("Not yet implemented")
    }

}