package com.github.fribourgsdp.radio

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener

interface Database {

    /**
     * Gets the [User], wrapped in a [Task], linked to the [userId] given, [userId] should be the authentication token
     * @return [User] wrapped in a task, if the [userId] doesn't exists, it returns a null [User]
     */
   fun getUser(userId : String): Task<User>

   /**
    * Sets the [User] information in the database and link it the to [userId] given, [userId] should be the authentication token
    */
   fun setUser(userId : String, user : User): Task<Void>

    /**
     * Gets the [Song], wrapped in a [Task], given its [songName]
     * @return [Song], wrapped in a [Task], the [Song] is null if it doesn't exist
     */
   fun getSong(songName : String): Task<Song>

    /**
     * Register the [Song] in the database
     */
   fun registerSong(song : Song): Task<Void>

    /**
     * Get the [Playlist], wrapped in a [Task], given its [playlistName]
     * @return [Playlist], wrapped in a [Task], the [Playlist] is null if it doesn't exist
     * @Note the [Song] in the [Playlist] have empty [artistName] and [lyrics]
     */
   fun getPlaylist(playlistName : String): Task<Playlist>

    /**
     * Register the [Playlist] in the database
     */
   fun registerPlaylist( playlist : Playlist): Task<Void>

    /**
     * Get a unique ID for a lobby. It is an asynchronous operation, so it is returned in a task.
     * @return a task loading a unique ID for the lobby.
     */
    fun getLobbyId() : Task<Long>

    /**
     * Open a lobby on the database.
     * @return a task void so that we know if the lobby was correctly opened.
     */
    fun openLobby(id: Long, settings : Game.Settings) : Task<Void>

    /**
     * Listen to the updates of the lobby with [id].
     * Executes the [listener] on every update.
     */
    fun listenToLobbyUpdate(id: Long, listener: EventListener<DocumentSnapshot>)

    /**
     * Get the [Game.Settings] of the lobby [id]
     * @return a task loading the [Game.Settings] of the lobby [id].
     */
    fun getGameSettingsFromLobby(id: Long) :Task<Game.Settings>

    /**
     * Add [user] to the lobby [id].
     * @return a task void  so that we know if the [user] was correctly added to the lobby [id].
     */
    fun addUserToLobby(id: Long, user: User) : Task<Void>

    /**
     * Open a game on the database.
     * @return a task void so that we know if the lobby was correctly opened.
     */
    fun openGame(id: Long) : Task<Void>

    /**
     * Launches a game from the lobby.
     * @return a task void so that we know if a game was correctly launched.
     */
    fun launchGame(id: Long) : Task<Void>

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
    fun updateGame(id: Long, updatesMap: Map<String, Any>) : Task<Void>

    /**
     * Update the current song to [songName] in the game [id].
     * @return a task void so that we know if the current song of the game was correctly updated.
     */
    fun updateCurrentSongOfGame(id: Long, songName: String) : Task<Void> {
        return updateGame(id, hashMapOf("current_song" to songName))
    }

    /**
     * Set the player with id [playerID] to done in the metadata of game [gameID]
     * @return a task void so that we know if the player with id [playerID] was correctly set to done in the metadata of game [gameID].
     */
    fun setPlayerDone(gameID: Long, playerID: String): Task<Void>

    /**
     * Reset the map where players are done or not to false for everyone but the singer.
     * @return a task void so that we know if the map was correctly reset.
     */
    fun resetPlayerDoneMap(gameID: Long, singer: String): Task<Void>

}