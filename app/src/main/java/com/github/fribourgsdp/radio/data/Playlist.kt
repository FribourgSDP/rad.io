package com.github.fribourgsdp.radio.data

import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.database.FirestoreDatabase
import com.github.fribourgsdp.radio.external.musixmatch.LyricsGetter
import com.github.fribourgsdp.radio.external.musixmatch.MusixmatchLyricsGetter
import com.github.fribourgsdp.radio.util.SetUtility
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
     * getter for a single song, which has the same name and artist
     *
     * @param name the name of the song we are trying to retrieve
     * @param artist the artist of the song we are trying to retrieve
     * @return the requested song
     * @throws NoSuchFileException
     */
    fun getSong(name: String, artist: String): Song {
        return SetUtility.getNamedFromSet(songs, Song(name, artist))
    }

    /**
     * This method transforms the playlist to a playlist that can be stored online
     * by generating id's for all songs and the playlist itself
     * @param db the database to fetch the id from, by default FirestoreDatabase
     * @return [Task] indicating containing all tasks received from the database
     */
    fun transformToOnline(db : Database = FirestoreDatabase()): Task<Void> {
        val songTask =  db.generateSongIds(songs.size).continueWith {songIdRange ->
            for ((i, song) in songs.withIndex()) {
                song.id = (songIdRange.result.first + i).toString()
            }
        }
        val playlistId =  db.generatePlaylistId().continueWith{l ->
            id = l.result.toString()
        }
        return Tasks.whenAll(songTask,playlistId)
    }

    /**
     * Loads the lyrics of all the songs of the playlist that don't have lyrics
     * @param [lyricsGetter] the lyricsGetter that provides the lyrics, by default Musixmatch
     */
    fun loadLyrics(lyricsGetter: LyricsGetter = MusixmatchLyricsGetter) : Task<Void>{

        val tasks = mutableListOf<Task<Void>>()
        for (song in songs){
            if(song.lyrics == "") {
                tasks.add(
                    Tasks.forResult(lyricsGetter.getLyrics(song.name, song.artist).thenAccept {
                        song.lyrics = it
                    }.join())
                )
            }
        }
        return Tasks.whenAll(tasks)

    }
    
    /**
     * Save the playlist and its songs online
     * @param db the database where the playlist will be saved, by default FirestoreDatabase
     * @return [Task] the task of the registration of the playlist
     */
    fun saveOnline(db : Database = FirestoreDatabase()): Task<Void>{
        val tasks = mutableListOf<Task<Void>>()
        for( song in songs){
            tasks.add(db.registerSong(song))
        }
        tasks.add(db.registerPlaylist(this))
        savedOnline = true
        return Tasks.whenAllComplete(tasks).continueWith { null }

    }

    /**
     * @return true if all songs have lyrics or have tried to fetch lyrics from Musixmatch
     */
    fun allSongsHaveLyricsOrHaveTriedFetchingSome(): Boolean{
        return songs.all { it.lyrics != MusixmatchLyricsGetter.BACKEND_ERROR_PLACEHOLDER  }
    }

    /**
     * @return true if all song has lyrics
     */
    fun allSongHaveLyrics(): Boolean{
        return songs.all { it.songHasLyrics()}
    }

    /**
     * @return true if no song has lyrics
     */
    fun noSongHaveLyrics(): Boolean{
        return songs.all { !it.songHasLyrics()}
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