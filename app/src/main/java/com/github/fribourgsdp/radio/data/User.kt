package com.github.fribourgsdp.radio.data


import android.content.Context
import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.database.DatabaseHolder
import com.github.fribourgsdp.radio.persistence.*
import com.github.fribourgsdp.radio.database.FirestoreDatabase
import com.github.fribourgsdp.radio.util.SetUtility

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import kotlin.random.Random
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


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
data class User (var name: String, val color: Int) : SavesToFileSystem<User>(USER_DATA_PATH) {
    init {
        require(name.isNotEmpty() && name.isNotBlank())
    }

    constructor(name: String): this(name, generateColor())

    private val playlists = mutableSetOf<Playlist>()
    private val playlistNamesToSpotifyId = mutableMapOf<String, String>()
    var linkedSpotify: Boolean = false
    val initial get(): Char = name.elementAt(0)
    var id : String = "def"
    var isGoogleUser = false


    companion object : LoadsFromFileSystem<User>(),DatabaseHolder {
        const val USER_DATA_PATH = "user_data_file"
        override var defaultPath = USER_DATA_PATH
        var database : Database = FirestoreDatabase();

        override fun load(context: Context, path: String) : User {
            val fs = fileSystemGetter.getFileSystem(context)
            return Json.decodeFromString(fs.read(path))
        }

        /**
         * Tries to load a user from the app-specific storage on the device.
         * If it fails, it returns a default [User]
         *
         * @param context the context to use for loading from a file (usually this in an activity)
         * @return the user saved on the device if it exists or a default [User] otherwise
         */
        fun loadOrDefault(context: Context) : Task<User> {
            return try {
                Tasks.forResult(load(context))
            } catch (e: java.io.FileNotFoundException) {
                createDefaultUser()
                    .addOnSuccessListener { user ->
                    user.save(context)
                }
            }
        }

        /**
         * Create a [User] with default settings.
         *
         * @return a default [User] in task, as an id has to be generated
         */
        fun createDefaultUser(): Task<User> {
            return database.generateUserId().continueWith { id ->
                val generatedUser = User("Guest", generateColor())
                generatedUser.id = id.result.toString()
                generatedUser
            }

        }

        /**
         * generates a random color Int which can be used for a User's color param
         */
        fun generateColor(): Int = (255 shl 24) or
                (Random.nextInt(100, 200) shl 16) or
                (Random.nextInt(100, 200) shl 8) or
                Random.nextInt(100, 200)
    }


    override fun save(context: Context, path: String){
        val fs = fileSystemGetter.getFileSystem(context)
        fs.write(path, Json.encodeToString(this))
    }

    /**
     * Adds a single playlist to the user's set of playlists
     * If the set already contains a playlist with the same name, it is replaced.
     * @param playlist the playlist to add
     */
    fun addPlaylist(playlist: Playlist){
        SetUtility.addToSet(playlists, playlist)
    }

    /**
     * Adds multiple playlists to the user's set of playlists
     * If the user playlists already contains a playlist with the same name as one in the given set, it is replaced.
     * @param addedPlaylists the set of playlists to add
     */
    fun addPlaylists(addedPlaylists: Set<Playlist>){
        SetUtility.addAllToSet(playlists, addedPlaylists)
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

    /**
     * getter for a single playlist, matched according to the name give
     *
     * @param name the name of the playlist we are trying to retrieve
     * @return the requested playlist
     * @throws NoSuchFileException
     */
    fun getPlaylistWithName(name: String): Playlist {
        return SetUtility.getNamedFromSet(playlists, Playlist(name))
    }

    /**
     * Modifies a song in a playlist of the user
     *
     * @param playlist the name of the playlist we are trying to modify
     * @param song the name of the song we are trying to modify or add
     */
    fun updateSongInPlaylist(playlist: Playlist, song: Song){
        getPlaylistWithName(playlist.name).addSong(song)
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
}