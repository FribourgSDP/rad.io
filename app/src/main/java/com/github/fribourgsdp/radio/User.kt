package com.github.fribourgsdp.radio

import android.content.Context
import kotlin.random.Random
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.io.File

const val USER_DATA_PATH = "user_data_file"

@Serializable
class User (val name: String, val color: Int) {
    init {
        require(name.isNotEmpty() && name.isNotBlank())
    }

    constructor(name: String): this(name, generateColor())

    private val playlists = mutableSetOf<Playlist>()
    private var linkedSpotify: Boolean = false
    val initial get(): Char = name.elementAt(0)
    val spotifyLinked get(): Boolean = linkedSpotify

    companion object {
        fun load( context: Context, path: String = USER_DATA_PATH,) : User { //TODO ERROR HANDLING
            val userFile = File(context.filesDir, path)
            return Json.decodeFromString<User>(userFile.readText())
        }

        fun generateColor(): Int = (255 shl 24) or
                (Random.nextInt(100, 200) shl 16) or
                (Random.nextInt(100, 200) shl 8) or
                Random.nextInt(100, 200)
    }

    fun save(context: Context){
        val userFile = File(context.filesDir, USER_DATA_PATH)
        userFile.writeText(Json.encodeToString(this))
    }

    fun addPlaylist(playlist: Playlist){
        playlists.add(playlist)
    }

    fun addPlaylists(addedPlaylists: Set<Playlist>){
        playlists.addAll(addedPlaylists)
    }

    fun removePlaylist(playlist: Playlist){
        playlists.remove(playlist)
    }

    fun removePlaylists(removedPlaylist: Set<Playlist>){
        playlists.removeAll(removedPlaylist)
    }

    fun getPlaylists(): Set<Playlist> {
        return playlists.toSet()
   }
}