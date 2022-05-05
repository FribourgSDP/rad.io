package com.github.fribourgsdp.radio

import org.junit.Test
import org.junit.Assert.*
import com.github.fribourgsdp.radio.util.StringComparisons

class StringComparisonsTest {
    @Test
    fun equalStringsTest(){
        assertEquals(0, StringComparisons.compare("rouge", "rouge"))
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
        assertEquals(1, StringComparisons.compare("rovge", "rouge"))
        assertEquals(1, StringComparisons.compare("rougf ", "rouge"))
        assertEquals(1, StringComparisons.compare("rovge", "rouge"))
        assertEquals(1, StringComparisons.compare("  Wish, yoO  ~  WERE-herE    ", "Wish you were here..."))
        assertEquals(1, StringComparisons.compare("àûầéèêẽ-íïöucç", "aaaeeee_iiouçĉ"))
        }
    @Test
    fun oneDifferenceStrings2(){
        assertEquals(1, StringComparisons.compare("roge", "rouge"))
        assertEquals(1, StringComparisons.compare("rouuge", "rouge"))
        assertEquals(1, StringComparisons.compare("master of pupppets", "master of puppets"))
        assertEquals(1, StringComparisons.compare("master of pupets", "master of puppets"))
        assertEquals(1, StringComparisons.compare("master of puppet", "master of puppets"))
    }
    @Test
    fun twoDifferencesStrings2(){
        assertEquals(2, StringComparisons.compare("rge", "rouge"))
        assertEquals(2, StringComparisons.compare("rouuuge", "rouge"))
        assertEquals(2, StringComparisons.compare("mastwr og puppets", "master of puppets"))
        assertEquals(2, StringComparisons.compare("master of pupet", "master of puppets"))
        assertEquals(2, StringComparisons.compare("master of puppppets", "master of puppets"))
    }
    @Test
    fun differentStrings(){
        assertNotEquals(1, StringComparisons.compare("rge", "rouge"))
        assertNotEquals(0, StringComparisons.compare("rougf", "rouge"))
    }

}