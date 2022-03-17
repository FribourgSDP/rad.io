package com.github.fribourgsdp.radio

import org.junit.Test

import org.junit.Assert.*

internal class SongTest {
    @Test
    fun primary_constructor_works(){
        val test: Song = Song("Song Name", "bob", "lyrics really basic")
        assertEquals("Song Name", test.name)
        assertEquals("lyrics really basic", test.lyrics)
        assertEquals("Bob", test.artist)
    }


    @Test
    //In the case where a user creates a playlist, he may have mistakenly written the title wrong and may want to change it
    fun changing_song_name_is_possible() {
        val test: Song = Song("Song Name")
        test.name = "this new name is better"
        assertEquals("This New Name Is Better", test.name)
    }

    @Test
    fun title_formatting_gets_rid_of_redundant_whitespaces(){
        val test: Song = Song("Song     Name   hihi ", "", "test lyrics")
        assertEquals("Song Name Hihi", test.name)
    }

    @Test
    fun title_formatting_makes_first_letter_of_every_word_uppercase(){
        val test: Song = Song("story Of My life loser hello", "", "test lyrics")
        assertEquals("Story Of My Life Loser Hello", test.name)
    }

    @Test
    fun addingArtistNameWorks(){
        val test: Song = Song("new song")
        test.artist = "best artist"
        assertEquals("Best Artist", test.artist)
    }

    @Test
    fun addingLyricsWorks(){
        val test: Song = Song("new song")
        test.lyrics = "louloulou these are great lyrics"
        assertEquals("louloulou these are great lyrics", test.lyrics)
    }

    @Test
    fun songsAreEqualIfSongNameAndArtistAreTheSame(){
        val test1: Song = Song("abc", "bob")
        val test2: Song = Song("abc", "bob")
        val test3: Song = Song("Abc", "bob")
        assertEquals(true, test1.equals(test2))
        assertEquals(true, test1.equals(test3))
    }

    @Test
    fun songsArentEqualIfOneOfArtistOrSongNameDiffers(){
        val test1: Song = Song("abc", "bobby")
        val test2: Song = Song("abc", "bob")
        val test3: Song = Song("Abcc", "bob")
        assertEquals(false, test1.equals(test2))
        assertEquals(false, test2.equals(test3))
    }

    @Test
    fun changingSongNameWorksAndSatisfiesFormat(){
        val test1: Song = Song("abc", "bobby")
        test1.name = "lala the    pig"
        assertEquals("Lala The Pig", test1.name)
    }

    @Test
    fun equalityHoldsWhenTitleAndArtistMatch(){
        val song1: Song = Song("a", "bob")
        val song2: Song = Song("A", "bob")
        assert(song1.equals(song2))
        assertEquals(song1, song2)
        val test :List<Song> = mutableListOf(song1)
        assert(test.contains(song2))
        val test2: Set<Song> = mutableSetOf(song1)
        assert(test2.contains(song2))
    }
}