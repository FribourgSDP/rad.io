package com.github.fribourgsdp.radio

import kotlinx.serialization.Serializable

@Serializable
data class Playlist (var name: String, var genre: Genre){

    private val songs: MutableSet<Song> = mutableSetOf()

    constructor(name: String, set: Set<Song>, genre: Genre) : this(name, genre) {
        this.addSongs(set)
    }

    constructor(playlistName: String): this(playlistName, Genre.NONE)


    fun addSong(song: Song){
        songs.add(song)
    }

    fun addSongs(addedSongs: Set<Song>){
        songs.addAll(addedSongs)
    }

    fun removeSong(song: Song){
        songs.remove(song)
    }

    fun removeSongs(removedSongs: Set<Song>){
        songs.removeAll(removedSongs)
    }

    fun combinePlaylists(other: Playlist, newName: String = name, newGenre: Genre = Genre.NONE) {
        name = newName
        genre = newGenre
        for (song in other.songs) {
            songs.add(Song(song.name, song.artist, song.lyrics))
        }
    }

    fun getSongs(): Set<Song> {
        return songs.toSet()
    }
}