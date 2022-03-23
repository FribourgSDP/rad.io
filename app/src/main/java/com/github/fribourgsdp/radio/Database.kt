package com.github.fribourgsdp.radio

import com.google.android.gms.tasks.Task

interface Database {

    /**
     * Gets the [User], wrapped in a [Task], linked to the [userId] given, [userId] should be the authentication token
     * @return [User] wrapped in a task, if the [userId] doesn't exists, it returns a null [User]
     */
   fun getUser(userId : String): Task<User?>

   /**
    * Sets the [User] information in the database and link it the to [userId] given, [userId] should be the authentication token
    */
   fun setUser(userId : String, user : User)

    /**
     * Gets the [Song], wrapped in a [Task], given its [songName]
     * @return [Song], wrapped in a [Task], the [Song] is null if it doesn't exist
     */
   fun getSong(songName : String): Task<Song?>

    /**
     * Register the [Song] in the database
     */
   fun registerSong(song : Song)

    /**
     * Get the [Playlist], wrapped in a [Task], given its [playlistName]
     * @return [Playlist], wrapped in a [Task], the [Playlist] is null if it doesn't exist
     */
   fun getPlaylist(playlistName : String): Task<Playlist?>

    /**
     * Register the [Playlist] in the database
     */
   fun registerPlaylist( playlist : Playlist)
}