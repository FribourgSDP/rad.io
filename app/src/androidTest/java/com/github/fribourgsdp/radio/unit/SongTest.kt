package com.github.fribourgsdp.radio.unit

import com.github.fribourgsdp.radio.data.Song
import org.junit.Test

import org.junit.Assert.*
import java.util.concurrent.CompletableFuture

internal class SongTest {
    @Test
    fun primary_constructor_works(){
        val test = Song("Song Name", "bob", "lyrics really basic")
        assertEquals("Song Name", test.name)
        assertEquals("lyrics really basic", test.lyrics)
        assertEquals("Bob", test.artist)
    }

    @Test
    fun title_formatting_gets_rid_of_redundant_whitespaces(){
        val test = Song("Song     Name   hihi ", "", "test lyrics")
        assertEquals("Song Name Hihi", test.name)
    }

    @Test
    fun title_formatting_makes_first_letter_of_every_word_uppercase(){
        val test = Song("story Of My life loser hello", "", "test lyrics")
        assertEquals("Story Of My Life Loser Hello", test.name)
    }

    @Test
    fun songsAreEqualIfSongNameAndArtistAreTheSame(){
        val test1 = Song("abc", "bob")
        val test2 = Song("abc", "bob")
        val test3 = Song("Abc", "bob")
        assertTrue(test1 == test2)
        assertTrue(test1 == test3)
    }

    @Test
    fun songsArentEqualIfOneOfArtistOrSongNameDiffers(){
        val test1 = Song("abc", "bobby")
        val test2 = Song("abc", "bob")
        val test3 = Song("Abcc", "bob")
        assertFalse(test1 == test2)
        assertFalse(test2 == test3)
    }

    @Test
    fun equalityHoldsWhenTitleAndArtistMatch(){
        val song1 = Song("a", "bob")
        val song2 = Song("A", "bob")
        assertTrue(song1 == song2)
        assertEquals(song1, song2)
        val test :List<Song> = mutableListOf(song1)
        assertTrue(test.contains(song2))
        val test2: Set<Song> = mutableSetOf(song1)
        assertTrue(test2.contains(song2))
    }

    @Test
    fun testCompletableFutureConstructor(){
        val testLyrics = "test"
        val song = Song("name", "artist", CompletableFuture.completedFuture(testLyrics))
        assertEquals(testLyrics,song.lyrics)
    }
}