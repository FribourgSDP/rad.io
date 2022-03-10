package com.github.fribourgsdp.radio

class Playlist (playlistName: String, songSet: Set<Song>, genreType: Genre){
    var name: String = playlistName
    private val songs: MutableSet<Song>
    var genre: Genre = genreType

    init {
        val result = mutableSetOf<Song>()
        for (song in songSet){
            result.add(Song(song.name, song.artist, song.lyrics))
        }
        songs = result
    }
    constructor(playlistName: String, genreType: Genre): this(playlistName, mutableSetOf(), genreType)
    constructor(playlistName: String): this(playlistName, mutableSetOf(), Genre.NONE)


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
        //return songs.toMutableSet()
        return songs.toSet()
    }
}