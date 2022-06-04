package com.github.fribourgsdp.radio.unit

import com.github.fribourgsdp.radio.utils.testPlaylist1
import com.github.fribourgsdp.radio.data.Genre
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.external.musixmatch.MusixmatchLyricsGetter
import com.github.fribourgsdp.radio.mockimplementations.MockLyricsGetter
import com.github.fribourgsdp.radio.utils.testLyrics6
import com.github.fribourgsdp.radio.utils.KotlinAny
import com.google.android.gms.tasks.Tasks
import org.junit.Test

import org.junit.Assert.*
import org.mockito.Mockito.*

internal class PlaylistTest {

    private val song1 = Song("colorado", "Milky Chance")
    private val song2 = Song("Back in black", "ACDC")
    private val song3 = Song("i got a feeling", "black eyed pees")
    private val song4 = Song("  party rock anthem", "lmfao")
    private val song5 = Song("song song", "artist", "bla")
    private val song6 = Song("a", "aa")
    private val song7 = Song("woodlawn", "amine")
    private val song1Lyrics = "Lyrics1"
    private val song2Lyrics = "Lyrics2"
    private val song3Lyrics = "Lyrics3"
    private val song4Lyrics = "Lyrics4"
    private val firstPlaylistName = "first"

    @Test
    fun primaryConstructor(){
        val playlist1 = Playlist(firstPlaylistName, mutableSetOf(song1, song2, song3, song4), Genre.POP)
        assertEquals(Genre.POP, playlist1.genre)
        assertEquals(firstPlaylistName, playlist1.name)
        assertEquals(4, playlist1.getSongs().size)
    }

    @Test
    fun doesConstructorDoADeepCopy(){
        val set = mutableSetOf(song1, song2, song3, song4)
        val playlist1 = Playlist(firstPlaylistName, set, Genre.POP)
        set.remove(song1)
        assertEquals(4, playlist1.getSongs().size)
    }

    @Test
    fun constructorWithNameAndGenreWorks(){
        val playlist1 = Playlist("Second", Genre.ROCK)
        assertEquals(0, playlist1.getSongs().size)
        playlist1.addSong(song5)
        assertEquals(1, playlist1.getSongs().size)
        assertTrue(playlist1.getSongs().contains(song5))
    }

    @Test
    fun constructorWithOnlyNameWorks(){
        val playlist1 = Playlist("Second")
        assertEquals(0, playlist1.getSongs().size)
        assertEquals(Genre.NONE, playlist1.genre)
        playlist1.genre = Genre.ROCK
        assertEquals(Genre.ROCK, playlist1.genre)
    }

    @Test
    fun getSet() {
        val testerSet = mutableSetOf(song1, song2, song3, song4)
        val playlist1 = Playlist(firstPlaylistName, testerSet, Genre.POP)
        val songSet = playlist1.getSongs()
        assertFalse(testerSet === songSet)
        assertTrue(playlist1.getSongs().contains(song1))
    }

    @Test
    fun addSong() {
        val testerSet = mutableSetOf(song1, song2, song3, song4)
        val playlist1 = Playlist(firstPlaylistName, testerSet, Genre.POP)
        val song = Song("new song", "new artist", "bla")
        val almostSameSong = Song("new Song", "New artist")
        playlist1.addSong(song)
        assertTrue(playlist1.getSongs().contains(almostSameSong))
        assertEquals(5, playlist1.getSongs().size)
    }

    @Test
    fun addSongs() {
        val testerSet = mutableSetOf(song1, song2, song3, song4)
        val playlist1 = Playlist(firstPlaylistName, testerSet, Genre.POP)
        val addedSet: Set<Song> = mutableSetOf(Song(song6.name, song6.artist), Song(song7.name, song7.artist))
        playlist1.addSongs(addedSet)
        assertEquals(6, playlist1.getSongs().size)
        assertTrue(playlist1.getSongs().contains(Song(song6.name, song6.artist)))
        assertTrue(playlist1.getSongs().contains(Song(song7.name, song7.artist)))
    }

    @Test
    fun removeSong() {
        val testerSet = mutableSetOf(song1, song2, song3, song4)
        val playlist1 = Playlist(firstPlaylistName, testerSet, Genre.POP)
        playlist1.removeSong(Song(song2.name, song2.artist))
        assertEquals(3, playlist1.getSongs().size)
        assertFalse(playlist1.getSongs().contains(Song(song2.name, song2.artist)))
    }

    @Test
    fun removeSongs() {
        val testerSet = mutableSetOf(song1, song2, song3, song4)
        val playlist1 = Playlist(firstPlaylistName, testerSet, Genre.POP)
        val removeSet = mutableSetOf(song1, song2)
        playlist1.removeSongs(removeSet)
        assertEquals(2, playlist1.getSongs().size)
        assertFalse(playlist1.getSongs().contains(Song(song2.name, song2.artist)))
        assertFalse(playlist1.getSongs().contains(Song(song1.name, song1.name)))
    }

    @Test
    fun combiningPlaylistsAppliesNewPlaylistNameAndHasSongsFromBoth(){
        val testerSet1 = mutableSetOf(song1, song2)
        val testerSet2 = mutableSetOf(song2, song3, song4)
        val playlistToBeAdded = Playlist("old", testerSet1, Genre.NONE)
        val playlistAddedTo = Playlist("new", testerSet2, Genre.POP)
        playlistAddedTo.combinePlaylists(playlistToBeAdded, "New Playlist", Genre.HIPHOP)
        assertEquals(Genre.HIPHOP, playlistAddedTo.genre)
        assertEquals("New Playlist", playlistAddedTo.name)
        assertEquals(4, playlistAddedTo.getSongs().size)
        assertTrue(playlistAddedTo.getSongs().contains(song1))
        assertTrue(playlistAddedTo.getSongs().contains(song2))
    }


    @Test
    fun combiningPlaylistsWorksWithDefaultValues(){
        val testerSet1 = mutableSetOf(song1, song2)
        val testerSet2 = mutableSetOf(song2, song3, song4)
        val playlistToBeAdded = Playlist("old", testerSet1, Genre.NONE)
        val playlistAddedTo = Playlist("new", testerSet2, Genre.POP)
        playlistAddedTo.combinePlaylists(playlistToBeAdded)
        assertEquals(Genre.NONE, playlistAddedTo.genre)
        assertEquals("new", playlistAddedTo.name)
        assertEquals(4, playlistAddedTo.getSongs().size)
        assertTrue(playlistAddedTo.getSongs().contains(song1))
        assertTrue(playlistAddedTo.getSongs().contains(song2))
    }

    @Test
    fun getPlaylistWithNameWorks(){
        val testerSet = mutableSetOf(song1, song2, song3, song4)
        val playlist = Playlist(firstPlaylistName, testerSet, Genre.POP)
        assertEquals(song1, playlist.getSong(song1.name, song1.artist))
    }

    @Test(expected = NoSuchElementException::class)
    fun getNonExistingPlaylistWithNameThrowsException(){
        val testerSet = mutableSetOf(song1, song2, song3, song4)
        val playlist = Playlist(firstPlaylistName, testerSet, Genre.POP)
        playlist.getSong("unknown", "any")
    }


    @Test
    fun transformOnlineWorks(){
        val songSet = mutableSetOf(song1,song2,song3,song4)
        val mockDB = mock(Database::class.java)
        `when`(mockDB.generateSongIds(anyInt())).thenReturn(Tasks.forResult(Pair(0,songSet.size.toLong())))
        `when`(mockDB.generatePlaylistId()).thenReturn(Tasks.forResult(0))
        val playlist = Playlist("Test",songSet, Genre.NONE)
        Tasks.await(playlist.transformToOnline(mockDB))
        assertEquals("0",playlist.id)
        val songs = playlist.getSongs()
        assertEquals(1,songs.filter { it.id == "0" }.size)
        assertEquals(1,songs.filter { it.id == "1" }.size)
        assertEquals(1,songs.filter { it.id == "2" }.size)
        assertEquals(1,songs.filter { it.id == "3" }.size)

    }

    @Test
    fun saveOnlineWorks(){
        val songSet = mutableSetOf(song1,song2,song3,song4)
        val mockDB = mock(Database::class.java)
        `when`(mockDB.generateSongIds(anyInt())).thenReturn(Tasks.forResult(Pair(0,songSet.size.toLong())))
        `when`(mockDB.generatePlaylistId()).thenReturn(Tasks.forResult(0))
        `when`(mockDB.registerSong(KotlinAny.any())).thenReturn(Tasks.forResult(null))
        `when`(mockDB.registerPlaylist(KotlinAny.any())).thenReturn(Tasks.forResult(null))


        val playlist = Playlist(testPlaylist1,songSet, Genre.NONE)
        Tasks.await(playlist.transformToOnline(mockDB))
        playlist.saveOnline()
        assertEquals(true,playlist.savedOnline)
    }

    @Test
    fun loadLyricsLoadsLyricsAndUpdateSongInPlaylist(){
        val songSet = mutableSetOf<Song>(song1,song2,song3,song4)
        val playlist = Playlist(firstPlaylistName,songSet,Genre.JAZZ)

        Tasks.await(playlist.loadLyrics(MockLyricsGetter))
        assertEquals(testLyrics6,playlist.getSong(song1.name,song1.artist).lyrics)
        assertEquals(testLyrics6,playlist.getSong(song2.name,song2.artist).lyrics)
        assertEquals(testLyrics6,playlist.getSong(song3.name,song3.artist).lyrics)
        assertEquals(testLyrics6,playlist.getSong(song4.name,song4.artist).lyrics)

    }

    @Test
    fun allSongsAndNoSongHaveLyricsWorks(){
        val songSet = mutableSetOf<Song>(song1,song2,song3,song4)
        val playlist = Playlist(firstPlaylistName,songSet,Genre.JAZZ)
        song4.lyrics = MusixmatchLyricsGetter.BACKEND_ERROR_PLACEHOLDER
        assertTrue(playlist.noSongHaveLyrics())
        song1.lyrics = song1Lyrics
        song2.lyrics = song2Lyrics
        song3.lyrics = song3Lyrics
        assertFalse(playlist.noSongHaveLyrics())
        assertFalse(playlist.allSongsHaveLyricsOrHaveTriedFetchingSome())
        song4.lyrics = MusixmatchLyricsGetter.LYRICS_NOT_FOUND_PLACEHOLDER
        assertTrue(playlist.allSongsHaveLyricsOrHaveTriedFetchingSome())
        song4.lyrics = song4Lyrics
        assertTrue(playlist.allSongHaveLyrics())

    }
}