package com.github.fribourgsdp.radio.unit

import com.github.fribourgsdp.radio.activities.testSong1
import org.junit.Test
import org.junit.Assert.*
import com.github.fribourgsdp.radio.util.StringComparisons

class StringComparisonsTest {
    @Test
    fun equalStringsTest(){
        assertEquals(0, StringComparisons.compare(testSong1, testSong1))
    }
    @Test
    fun spacefulStringsTest(){
        assertEquals(0, StringComparisons.compare("  wish you    were  here    ", "wish you were here"))
    }
    @Test
    fun differentCasesStringsTest(){
        assertEquals(0, StringComparisons.compare("  Wish you    WERE  herE    ", "wish you were here"))
    }
    @Test
    fun veryDecoratedStringsTest(){
        assertEquals(0,  StringComparisons.compare("  Wish, you  ~  WERE-herE    ", "Wish you were here..."))
    }
    @Test
    fun accentedStrings(){
        assertEquals(0, StringComparisons.compare("àäầéèêẽ-íïőucç", "aaaeeee_iiouçĉ"))
    }
    @Test
    fun oneDifferenceStrings1(){
        assertEquals(1, StringComparisons.compare("rovge", testSong1))
        assertEquals(1, StringComparisons.compare("rougf ", testSong1))
        assertEquals(1, StringComparisons.compare("rovge", testSong1))
        assertEquals(1, StringComparisons.compare("  Wish, yoO  ~  WERE-herE    ", "Wish you were here..."))
        assertEquals(1, StringComparisons.compare("àûầéèêẽ-íïöucç", "aaaeeee_iiouçĉ"))
        }
    @Test
    fun oneDifferenceStrings2(){
        assertEquals(1, StringComparisons.compare("roge", testSong1))
        assertEquals(1, StringComparisons.compare("rouuge", testSong1))
        assertEquals(1, StringComparisons.compare("master of pupppets", "master of puppets"))
        assertEquals(1, StringComparisons.compare("master of pupets", "master of puppets"))
        assertEquals(1, StringComparisons.compare("master of puppet", "master of puppets"))
    }
    @Test
    fun twoDifferencesStrings2(){
        assertEquals(2, StringComparisons.compare("rge", testSong1))
        assertEquals(2, StringComparisons.compare("rouuuge", testSong1))
        assertEquals(2, StringComparisons.compare("mastwr og puppets", "master of puppets"))
        assertEquals(2, StringComparisons.compare("master of pupet", "master of puppets"))
        assertEquals(2, StringComparisons.compare("master of puppppets", "master of puppets"))
    }
    @Test
    fun differentStrings(){
        assertNotEquals(1, StringComparisons.compare("rge", testSong1))
        assertNotEquals(0, StringComparisons.compare("rougf", testSong1))
    }

}