package com.github.fribourgsdp.radio.mockimplementations

import com.github.fribourgsdp.radio.*
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

    override fun generateSongIds(number: Int): Task<Pair<Long, Long>> {
        TODO("Not yet implemented")
    }

    override fun generateUserId(): Task<Long> {
        return Tasks.forResult(EXPECTED_USER_UID)
    }

    override fun generatePlaylistId(): Task<Long> {
        TODO("Not yet implemented")
    }

    override fun generateSongId(): Task<Long> {
        TODO("Not yet implemented")
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

    override fun addUserToLobby(id: Long, user: User, hasMicPermissions: Boolean): Task<Void> {
        return Tasks.forResult(null)
    }

    override fun modifyUserMicPermissions(id: Long, user: User, newPermissions: Boolean): Task<Void> {
        return Tasks.forResult(null)
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
        return Tasks.forResult(null)    }

    override fun disableGame(id: Long): Task<Void> {
        return Tasks.forResult(null)    }

    override fun disableLobby(gameID: Long): Task<Void> {
        return Tasks.forResult(null)    }

    override fun removePlayerFromGame(gameID: Long, user: User): Task<Void> {
        return Tasks.forResult(null)    }

    override fun makeSingerDone(gameID: Long, singerName: String): Task<Void> {
        TODO("Not yet implemented")
    }

    companion object {
        const val EXPECTED_USER_UID = 392L
        const val EXPECTED_UID = 794L
        val EXPECTED_SETTINGS = Game.Settings("Host", "Hello World!", "Host's Playlist", 42, true, true)

    }
}