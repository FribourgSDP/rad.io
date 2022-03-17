package com.github.fribourgsdp.radio

import kotlin.random.Random
import kotlinx.serialization.Serializable

@Serializable
class User (val name: String, val color: Int) {
    init {
        require(name.isNotEmpty() && name.isNotBlank())
    }
    private val playlists = mutableSetOf<Playlist>()
    private var linkedSpotify: Boolean = false
    val initial get(): Char = name.elementAt(0)
    val spotifyLinked get(): Boolean = linkedSpotify

    companion object {
        fun load(path: String) : User {
            TODO("implement load")
        }

        fun generateColor(): Int = (255 shl 24) or
                (Random.nextInt(100, 200) shl 16) or
                (Random.nextInt(100, 200) shl 8) or
                Random.nextInt(100, 200)
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