package com.github.fribourgsdp.radio.data

import android.content.Context
import android.util.Log
import com.github.fribourgsdp.radio.data.Genre.*
import com.github.fribourgsdp.radio.external.musixmatch.MusixmatchLyricsGetter
import java.io.BufferedReader
import java.io.InputStreamReader

object StarterPlaylists {
    private val top100french = Playlist("Chanson française", FRENCH)
    private val basicBlindTest = Playlist("Basic blind test", NONE)
    private val movieThemeSongs = Playlist("Movie theme songs", MOVIE)
    private val videoGames = Playlist("Video game songs", VIDEO_GAMES)
    private val classicalHits = Playlist("Classical music hits", CLASSICAL)

    private fun addSongArtistToPlaylist(playlist: Playlist, song: Pair<String, String>, lyrics : String) {
        val lyrics2 = lyrics.ifEmpty {
            MusixmatchLyricsGetter.LYRICS_NOT_FOUND_PLACEHOLDER
        }
        playlist.addSong(Song(song.first, song.second, lyrics2))
    }
    private fun addArtistSongToPlaylist(playlist: Playlist, song: Pair<String, String>, lyrics : String) {
        val lyrics2 = lyrics.ifEmpty {
            MusixmatchLyricsGetter.LYRICS_NOT_FOUND_PLACEHOLDER
        }
        playlist.addSong(Song(song.second, song.first, lyrics2))
    }


    /**
     * Function to get attributes of this object, namely the playlist created above.
     */
    fun getStarterPlaylists(context: Context): Set<Playlist> {
        readIntoPlaylist(context, basicBlindTest, "basicBlindTest.txt", ::addSongArtistToPlaylist)
        readIntoPlaylist(context, classicalHits, "classicalHits.txt", ::addSongArtistToPlaylist)
        readIntoPlaylist(context, movieThemeSongs, "movieThemeSongs.txt", ::addSongArtistToPlaylist)
        readIntoPlaylist(context, videoGames, "videoGames.txt", ::addSongArtistToPlaylist)
        readIntoPlaylist(context, top100french, "top100French.txt", ::addArtistSongToPlaylist)
        return setOf(basicBlindTest, classicalHits, movieThemeSongs, videoGames, top100french)
    }

    private fun readIntoPlaylist(
        context: Context,
        playlist: Playlist,
        filePath: String,
        addingFunction: (Playlist, Pair<String, String>, String) -> Unit
    ) {
        val reader = BufferedReader(InputStreamReader(context.assets.open(filePath)))
        var line = reader.readLine();
        while (line != null) {
            val tuple = line.split(":")
            val lyrics = tuple[2].replace("\\n", "\n")
            addingFunction.invoke(playlist, Pair(tuple[0], tuple[1]), lyrics)
            line = reader.readLine();
        }
        reader.close()
    }
}
