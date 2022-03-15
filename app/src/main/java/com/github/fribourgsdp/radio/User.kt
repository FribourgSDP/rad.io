package com.github.fribourgsdp.radio

import android.graphics.Color
import java.lang.Exception
import java.lang.IllegalArgumentException
import kotlin.random.Random

class User (nameInput: String) {
    // TODO IMPLEMENT SERIALIZATION
    // TODO IMPLEMENT STATIC METHODS TO LOAD + METHOD TO SAVE

    var name: String = nameInput
        set(value) {
            nameSanitizer(value)
            field = value
        }
    init {
        nameSanitizer(name)
    }
    private val playlists = mutableSetOf<Playlist>()
    val color : Int = (255 shl 24) or
        (Random.nextInt(100, 200) shl 16) or
        (Random.nextInt(100, 200) shl 8) or
        Random.nextInt(100, 200)
    private var linkedSpotify: Boolean = false
    val initial get(): Char = name.elementAt(0)
    val spotifyLinked get(): Boolean = linkedSpotify


    private fun nameSanitizer(name: String) {
        if (name.isEmpty() || name.isBlank()) {
            throw  IllegalArgumentException("name cannot be empty or blank string")
        }
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