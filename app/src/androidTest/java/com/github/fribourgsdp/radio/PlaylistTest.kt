package com.github.fribourgsdp.radio

import com.google.android.gms.tasks.Tasks
import org.junit.Test

import org.junit.Assert.*
import org.mockito.Mockito
import org.mockito.Mockito.*

internal class PlaylistTest {

    val song1 = Song("colorado", "Milky Chance")
    val song2 = Song("Back in black", "ACDC")
    val song3 = Song("i got a feeling", "black eyed pees")
    val song4 = Song("  party rock anthem", "lmfao")

    @Test
    fun primaryConstructor(){
        val playlist1 = Playlist("First", mutableSetOf(song1, song2, song3, song4), Genre.POP)
        assertEquals(Genre.POP, playlist1.genre)
        assertEquals("First", playlist1.name)
        assertEquals(4, playlist1.getSongs().size)
    }

    @Test
    fun doesConstructorDoADeepCopy(){
        val set = mutableSetOf(song1, song2, song3, song4)
        val playlist1 = Playlist("First", set, Genre.POP)
        set.remove(song1)
        assertEquals(4, playlist1.getSongs().size)
    }

    @Test
    fun constructorWithNameAndGenreWorks(){
        val playlist1 = Playlist("Second", Genre.ROCK)
        assertEquals(0, playlist1.getSongs().size)
        playlist1.addSong(Song("song song", "artist", "bla"))
        assertEquals(1, playlist1.getSongs().size)
        assertTrue(playlist1.getSongs().contains(Song("song song", "artist", "bla")))
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
        val playlist1 = Playlist("First", testerSet, Genre.POP)
        val songSet = playlist1.getSongs()
        assertFalse(testerSet === songSet)
        assertTrue(playlist1.getSongs().contains(Song("colorado", "Milky Chance")))
    }

    @Test
    fun addSong() {
        val testerSet = mutableSetOf(song1, song2, song3, song4)
        val playlist1 = Playlist("First", testerSet, Genre.POP)
        playlist1.addSong(Song("new song", "new artist", "bla"))
        val newSong = Song("new Song", "New artist")
        assertTrue(playlist1.getSongs().contains(newSong))
        assertEquals(5, playlist1.getSongs().size)
    }

    @Test
    fun addSongs() {
        val testerSet = mutableSetOf(song1, song2, song3, song4)
        val playlist1 = Playlist("First", testerSet, Genre.POP)
        val addedSet: Set<Song> = mutableSetOf(Song("a", "b"), Song("woodlawn", "amine"))
        playlist1.addSongs(addedSet)
        assertEquals(6, playlist1.getSongs().size)
        assertTrue(playlist1.getSongs().contains(Song("Woodlawn", "amine")))
        assertTrue(playlist1.getSongs().contains(Song("A", "b")))
    }

    @Test
    fun removeSong() {
        val testerSet = mutableSetOf(song1, song2, song3, song4)
        val playlist1 = Playlist("First", testerSet, Genre.POP)
        playlist1.removeSong(Song("back in black", "ACDC"))
        assertEquals(3, playlist1.getSongs().size)
        assertFalse(playlist1.getSongs().contains(Song("back in black", "ACDC")))
    }

    @Test
    fun removeSongs() {
        val testerSet = mutableSetOf(song1, song2, song3, song4)
        val playlist1 = Playlist("First", testerSet, Genre.POP)
        val removeSet = mutableSetOf(song1, song2)
        playlist1.removeSongs(removeSet)
        assertEquals(2, playlist1.getSongs().size)
        assertFalse(playlist1.getSongs().contains(Song("back in black", "ACDC")))
        assertFalse(playlist1.getSongs().contains(Song("colorado", "Milky Chance")))
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
        val playlist = Playlist("First", testerSet, Genre.POP)
        assertEquals(song1, playlist.getSong(song1.name))
    }

    @Test(expected = NoSuchElementException::class)
    fun getNonExistingPlaylistWithNameThrowsException(){
        val testerSet = mutableSetOf(song1, song2, song3, song4)
        val playlist = Playlist("First", testerSet, Genre.POP)
        assertEquals(song1, playlist.getSong("unknown"))
    }


    @Test
    fun transformOnlineWorks(){
        val songSet = mutableSetOf<Song>(song1,song2,song3,song4)
        val mockDB = mock(Database::class.java)
        `when`(mockDB.generateSongIds(anyInt())).thenReturn(Tasks.forResult(Pair(0,songSet.size.toLong())))
        `when`(mockDB.generatePlaylistId()).thenReturn(Tasks.forResult(0))
        val playlist = Playlist("Test",songSet,Genre.NONE)
        Tasks.await(playlist.transformToOnline(mockDB))
        assertEquals("0",playlist.id)
        val songs = playlist.getSongs().toList()
        assertEquals("0",songs[0].id)
        assertEquals("1",songs[1].id)
        assertEquals("2",songs[2].id)
        assertEquals("3",songs[3].id)
    }

    //this is usefull in order to be able to use any() from mockito
    private fun <T> any(): T {
        Mockito.any<T>()
        return uninitialized()
    }
    private fun <T> uninitialized(): T = null as T

    @Test
    fun saveOnlineWorks(){
        val songSet = mutableSetOf<Song>(song1,song2,song3,song4)
        val mockDB = mock(Database::class.java)
        `when`(mockDB.generateSongIds(anyInt())).thenReturn(Tasks.forResult(Pair(0,songSet.size.toLong())))
        `when`(mockDB.generatePlaylistId()).thenReturn(Tasks.forResult(0))
        `when`(mockDB.registerSong(any<Song>())).thenReturn(Tasks.forResult(null))
        `when`(mockDB.registerPlaylist(any())).thenReturn(Tasks.forResult(null))


        val playlist = Playlist("Test",songSet,Genre.NONE)
        Tasks.await(playlist.transformToOnline(mockDB))
        playlist.saveOnline()
        assertEquals(true,playlist.savedOnline)


    }
}