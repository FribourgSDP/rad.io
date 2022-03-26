package com.github.fribourgsdp.radio.backend.database

import com.github.fribourgsdp.radio.backend.gameplay.Game
import com.github.fribourgsdp.radio.backend.music.Playlist
import com.github.fribourgsdp.radio.backend.music.Song
import com.github.fribourgsdp.radio.backend.gameplay.User
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
}