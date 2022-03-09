package com.github.fribourgsdp.radio

import Genre

class Playlist (playListName: String, songSet: Set<Song>, genreType: Genre){
    var name: String = playListName
    private val songs: MutableSet<Song>
    var genre: Genre = genreType

    init {
        val result = mutableSetOf<Song>()
        for (song in songSet){
            result.add(Song(song.name, song.artist, song.lyrics))
        }
        songs = result
    }
    constructor(playListName: String, genreType: Genre): this(playListName, mutableSetOf(), genreType)
    constructor(playListName: String): this(playListName, mutableSetOf(), Genre.NONE)


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

    fun getSongs(): MutableSet<Song> {
        return songs.toMutableSet()
    }
}