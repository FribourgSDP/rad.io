package com.github.fribourgsdp.radio.database

import com.github.fribourgsdp.radio.*
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener

interface Database {

    /**
     * Gets the [User], wrapped in a [Task], linked to the [userId] given, [userId] should be the authentication token
     * @return [User] wrapped in a task, if the [userId] doesn't exists, it returns a null [User]
     */
    fun getUser(userId: String): Task<User>

    /**
     * Sets the [User] information in the database and link it the to [userId] given, [userId] should be the authentication token
     */
    fun setUser(userId: String, user: User): Task<Void>

    /**
     * Gets the [Song], wrapped in a [Task], given its [songName]
     * @return [Song], wrapped in a [Task], the [Song] is null if it doesn't exist
     */
    fun getSong(songName: String): Task<Song>

    /**
     * Register the [Song] in the database
     */
    fun registerSong(song: Song): Task<Void>

    /**
     * Get the [Playlist], wrapped in a [Task], given its [playlistName]
     * @return [Playlist], wrapped in a [Task], the [Playlist] is null if it doesn't exist
     * @Note the [Song] in the [Playlist] have empty artist and lyrics
     */
    fun getPlaylist(playlistName: String): Task<Playlist>

    /**
     * Register the [Playlist] in the database
     */
    fun registerPlaylist(playlist: Playlist): Task<Void>

    /**
     * Get a unique ID for a lobby. It is an asynchronous operation, so it is returned in a task.
     * @return a task loading a unique ID for the lobby.
     */
    fun getLobbyId(): Task<Long>

    /**
     * Generate of range of ID for songs. It is an asynchronous operation, so it is returned in a task.
     * @return a task loading a Pair of ID for songs, one for the lower bound and on for the upper bound.
     */
    fun generateSongIds(number: Int): Task<Pair<Long, Long>>

    /**
     * Get a unique ID for a user. It is an asynchronous operation, so it is returned in a task.
     * @return a task loading a unique ID for the user.
     */
    fun generateUserId(): Task<Long>

    /**
     * Get a unique ID for a playlist. It is an asynchronous operation, so it is returned in a task.
     * @return a task loading a unique ID for the playlist.
     */
    fun generatePlaylistId(): Task<Long>

    /**
     * Get a unique ID for a song. It is an asynchronous operation, so it is returned in a task.
     * @return a task loading a unique ID for the song.
     */
    fun generateSongId(): Task<Long>

    /**
     * Open a lobby on the database.
     * @return a task void so that we know if the lobby was correctly opened.
     */
    fun openLobby(id: Long, settings: Game.Settings): Task<Void>

    /**
     * Listen to the updates of the lobby with [id].
     * Executes the [listener] on every update.
     */
    fun listenToLobbyUpdate(id: Long, listener: EventListener<DocumentSnapshot>)

    /**
     * Get the [Game.Settings] of the lobby [id]
     * @return a task loading the [Game.Settings] of the lobby [id].
     */
    fun getGameSettingsFromLobby(id: Long): Task<Game.Settings>

    /**
     * Add [user] to the lobby [id] with [hasMicPermissions] microphone permissions.
     * @return a task void  so that we know if the [user] was correctly added to the lobby [id].
     */
    fun addUserToLobby(id: Long, user: User, hasMicPermissions: Boolean): Task<Void>

    /**
     * Get a list of all public [LobbyData].
     * @return a task loading the list of [LobbyData].
     */
    fun getPublicLobbies(): Task<List<LobbyData>>

    /**
     * Remove the lobby [id] from the public lobbies.
     * @return a task void so that we know if the lobby was correctly removed.
     */
    fun removeLobbyFromPublic(id: Long): Task<Void>

    /**
     * Listen to the updates of the public lobbies.
     * Executes the [listener] on every update.
     */
    fun listenToPublicLobbiesUpdate(listener: EventListener<List<LobbyData>>)

    /**
     * Modify [user]'s permission in [id]'th lobby with [newPermissions] microphone permissions.
     * @return a task void  so that we know if the [user] was correctly added to the lobby [id].
     */
    fun modifyUserMicPermissions(id: Long, user: User, newPermissions: Boolean): Task<Void>

    /**
     * Open a game on the database.
     * @return a task void so that we know if the game was correctly opened.
     */
    fun openGame(id: Long): Task<Void>

    /**
     * Open a game metadata document on the database.
     * @return a task void so that we know if the game metadata was correctly opened.
     */
    fun openGameMetadata(id: Long, usersIds: List<String>): Task<Void>

    /**
     * Launches a game from the lobby.
     * @return a task void so that we know if a game was correctly launched.
     */
    fun launchGame(id: Long): Task<Void>

    /**
     * Listen to the updates of the game with [id].
     * Executes the [listener] on every update.
     */
    fun listenToGameUpdate(id: Long, listener: EventListener<DocumentSnapshot>)

    /**
     * Listen to the updates of the metadata of game with [id].
     * Executes the [listener] on every update.
     */
    fun listenToGameMetadataUpdate(id: Long, listener: EventListener<DocumentSnapshot>)

    /**
     * Update the game [id] with the values in the [updatesMap].
     * @return a task void so that we know if the game was correctly updated.
     */
    fun updateGame(id: Long, updatesMap: Map<String, Any>): Task<Void>

    /**
     * Update the current song to [songName] in the game [id].
     * @return a task void so that we know if the current song of the game was correctly updated.
     */
    fun updateCurrentSongOfGame(id: Long, songName: String): Task<Void> {
        return updateGame(id, hashMapOf("current_song" to songName))
    }

    /**
     * End the turn of the player with id [playerID] in the metadata of game [gameID]
     * It sets that the player's turn was done and also whether the player [found][hasFound] the answer of not.
     * It also updates its score on the database.
     * @return a task void so that we know if the player's turn was ended correctly on the database.
     */
    fun playerEndTurn(gameID: Long, playerID: String, hasFound: Boolean = false): Task<Void>

    /**
     * Reset the game metadata on the [Database].
     * @return a task void so that we know if the game metadata was correctly reset on the [Database].
     */
    fun resetGameMetadata(gameID: Long, singer: String): Task<Void>

    /**
     * Removes [user] from lobby with [id] on the database.
     * @return a task void so we know the user was correctly removed.
     */
    fun removeUserFromLobby(id: Long, user: User): Task<Void>

    /**
     * Disable game with [id] on the database: set validity to false.
     * @return a task void so we know the lobby was correctly disabled.
     */
    fun disableGame(id: Long): Task<Void>

    /**
     * Disable lobby with [id] on the database: set validity to false.
     * @return a task void so we know the lobby was correctly disabled.
     */
    fun disableLobby(gameID: Long): Task<Void>

    /**
     * Removes [user] from game with [id] on the database.
     * @return a task void so we know the user was correctly removed.
     */
    fun removePlayerFromGame(gameID: Long, user: User): Task<Void>

    /**
     * Make player with name [singerName] done in game [gameID]
     * @return a task void so we know the singer was correctly added to the players done map in the database.
     */
    fun makeSingerDone(gameID: Long, singerName: String): Task<Void>
}