package com.github.fribourgsdp.radio


import android.content.Context
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import kotlin.random.Random

const val USER_DATA_PATH = "user_data_file"

@Serializable

/**
 * A data class for users with playlists
 * Serializable: supports the use of Json.encodeToString(<user>)
 * and Json.decodeFromString(<string>) so that it can be saved and loaded to files
 * or passed through intents as strings
 * (NOT as serializable Objects in intents, those implement Java.io.serializable)
 *
 * @property name name of the user (immutable)
 * @property color color associated to the user for use in game (immutable)
 * @property linkedSpotify true if User has successfully linked their spotify account
 * @property initial the initial of the user's name to use as visual user identification with their color
 *
 * @constructor creates a User
 */
data class User (var name: String, val color: Int) {
    init {
        require(name.isNotEmpty() && name.isNotBlank())
    }

    constructor(name: String): this(name, generateColor())

    private val playlists = mutableSetOf<Playlist>()
    private val playlistNamesToSpotifyId = mutableMapOf<String, String>()
    var linkedSpotify: Boolean = false
    val initial get(): Char = name.elementAt(0)
    var id : String = "def"


    companion object {
        /**
         * loads a user from the app-specific storage on the device.
         * There can only be a single User stored on the device at the default path
         * which is written with <user>.save().
         * This function can be used from any activity of the app and retrieves the same data
         *
         * @param context the context to use for loading from a file (usually this in an activity)
         * @param path a specific path in app-specific storage if we don't want to use the default
         *      user location
         * @throws java.io.FileNotFoundException
         *
         * @return the user saved on the device
         */
        fun load(context: Context, path: String = USER_DATA_PATH) : User {
            val userFile = File(context.filesDir, path)
            return Json.decodeFromString(userFile.readText())
        }

        /**
         * generates a random color Int which can be used for a User's color param
         */
        fun generateColor(): Int = (255 shl 24) or
                (Random.nextInt(100, 200) shl 16) or
                (Random.nextInt(100, 200) shl 8) or
                Random.nextInt(100, 200)

    }

    /**
     * saves a user to the app-specific storage on the device.
     * There can only be a single User stored on the device at the default path
     * This function can be used from any activity of the app to save data accessible from anywhere
     *
     * @param context the context to use for saving to a file (usually this in an activity)
     * @param path a specific path in app-specific storage if we don't want to use the default
     *      user location
     */
    fun save(context: Context, path: String = USER_DATA_PATH){
        val userFile = File(context.filesDir, path)
        userFile.writeText(Json.encodeToString(this))
    }

    /**
     * Adds a single playlist to the user's set of playlists
     *
     * @param playlist the playlist to add
     */
    fun addPlaylist(playlist: Playlist){
        playlists.add(playlist)
    }

    /**
     * Adds multiple playlists to the user's set of playlists
     *
     * @param addedPlaylists the set of playlists to add
     */
    fun addPlaylists(addedPlaylists: Set<Playlist>){
        playlists.addAll(addedPlaylists)
    }

    /**
     * removes a single playlist from the user's set of playlists
     *
     * @param playlist the playlist to remove
     */
    fun removePlaylist(playlist: Playlist){
        playlists.remove(playlist)
    }

    /**
     * Removes a playlist given by its name from the user's set of playlists
     * If multiple playlist share the same name, which should never happen, they will all be deleted.
     *
     * @param name the name of the playlist to remove
     */
    fun removePlaylistByName(name: String) : Boolean{
        return playlists.removeIf{p -> p.name == name}
    }

    /**
     * removes a set of playlists from the user's set of playlists
     *
     * @param removedPlaylist the playlists to remove
     */
    fun removePlaylists(removedPlaylist: Set<Playlist>){
        playlists.removeAll(removedPlaylist)
    }

    /**
     * getter for the playlists of a user
     * @return a copy of the playlists of a user
     */
    fun getPlaylists(): Set<Playlist> {
        return playlists.toSet()
    }

    fun addSpotifyPlaylistUId(playlistName: String, spotifyUid: String){
        playlistNamesToSpotifyId[playlistName] = spotifyUid
    }

    fun addSpotifyPlaylistUids(map: HashMap<String, String>){
        playlistNamesToSpotifyId.putAll(map)
    }

    fun getSpotifyPlaylistUId(playlistName: String): String? {
        return playlistNamesToSpotifyId[playlistName]
    }

    interface Loader {
        /**
         * Load the [User] of the device
         * @return the [User] of the device
         */
        fun loadUser(): User
    }
}