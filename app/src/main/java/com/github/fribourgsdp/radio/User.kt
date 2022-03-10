package com.github.fribourgsdp.radio

import android.graphics.Color
import java.lang.IllegalArgumentException
import kotlin.random.Random

class User (nameInput: String) {
    var name: String = nameInput
        set(value) {
            nameSanitizer(name)
            field = value
        }

    init {
        nameSanitizer(name)
    }

    private val playlists = mutableSetOf<Playlist>()
    val color : Int = Color.rgb(
        Random.nextInt(100, 200),
        Random.nextInt(100, 200),
        Random.nextInt(100, 200))
    private var linkedSpotify: Boolean = false

    private fun nameSanitizer(name: String) {
        if (name.isEmpty() || name.isBlank()) {
            throw  IllegalArgumentException("name cannot be empty or blank string")
        }
    }

    fun initial(): Char = name.elementAt(0)

    fun spotifyLinked(): Boolean = linkedSpotify

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