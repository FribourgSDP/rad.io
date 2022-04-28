package com.github.fribourgsdp.radio

import android.content.ContentValues
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import kotlinx.serialization.Serializable

@Serializable
/**
 * A data class for playlists
 * Serializable: supports the use of Json.encodeToString(<playlist>)
 * and Json.decodeFromString(<string>) so that it can be saved and loaded to files
 * or passed through intents as strings
 * (NOT as serializable Objects in intents, those implement Java.io.serializable)
 *
 * @property name name of the playlist
 * @property genre genre of the playlist
 * @constructor creates a Playlist with the given name and genre
 */
data class Playlist (override var name: String, var genre: Genre) : Nameable {

    private val songs: MutableSet<Song> = mutableSetOf()
    var id : String = ""
    var savedOnline = false
    var savedLocally = true
    constructor(name: String, set: Set<Song>, genre: Genre) : this(name, genre) {
        this.addSongs(set)
    }

    constructor(playlistName: String): this(playlistName, Genre.NONE)

    /**
     * Adds a single song to the playlist
     * We do not allow two song with the same name and artist. The given song might replace an already existing one.
     * @param song the song to add
     */
    fun addSong(song: Song){
        SetUtility.addToSet(songs, song)
    }

    /**
     * Adds multiple songs to the playlist
     * @param addedSongs the Set of songs to add
     */
    fun addSongs(addedSongs: Set<Song>){
        SetUtility.addAllToSet(songs, addedSongs)
    }

    /**
     * removes a single song from the playlist
     * @param song the song to remove
     */
    fun removeSong(song: Song){
        songs.remove(song)
    }

    /**
     * removes multiple songs from the playlist
     * @param removedSongs the Set of songs to remove
     */
    fun removeSongs(removedSongs: Set<Song>){
        songs.removeAll(removedSongs)
    }

    /**
     * transforms this playlist to contains the song of another playlist,
     * and possible change this playlists genre
     * @param other the playlist from which we should retrieve the songs
     * @param newName the new name to give to this playlist; defaults to current name
     * @param newGenre the new genre to give to this playlist; defaults to NONE
     */
    fun combinePlaylists(other: Playlist, newName: String = name, newGenre: Genre = Genre.NONE) {
        name = newName
        genre = newGenre
        for (song in other.songs) {
            songs.add(Song(song.name, song.artist, song.lyrics))
        }
    }

    /**
     * getter for the songs in the playlist
     * @return a copy of the songs in the playlist
     */
    fun getSongs(): Set<Song> {
        return songs.toSet()
    }

    /**
     * getter for a single song, matched according to the name give
     *
     * @param name the name of the song we are trying to retrieve
     * @return the requested song
     * @throws NoSuchFileException
     */
    fun getSong(name: String): Song {
        return SetUtility.getNamedFromSet(songs, name)
    }

    fun transformToOnline(): Task<Void> {
        val songTask =  FirestoreDatabase().generateSongIds(songs.size).continueWith {songIdRange ->
            for ((i, song) in songs.withIndex()) {
                song.id = (songIdRange.result.first + i).toString()
            }
        }
        val playlistId =  FirestoreDatabase().generatePlaylistId().addOnSuccessListener { l ->
            id = l.toString()
        }
        return Tasks.whenAll(songTask,playlistId)
    }

    fun saveOnline(): Task<Void>{
        val db = FirestoreDatabase()
        for( song in songs){
            db.registerSong(song)
        }
        savedOnline = true
        return db.registerPlaylist(this)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Playlist

        if (name != other.name) return false

        return true
    }

    override fun hashCode(): Int {
        return name.hashCode()
    }
}