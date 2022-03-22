package com.github.fribourgsdp.radio

import org.junit.Test

class StringComparisonsTest {
    @Test
    fun equalStringsTest(){
        assert(100 == StringComparisons.compare("rouge", "rouge", 100))
    }
    @Test
    fun spacefulStringsTest(){
        assert(100 == StringComparisons.compare("  wish you    were  here    ", "wish you were here", 100))
    }
    @Test
    fun differentCasesStringsTest(){
        assert(100 == StringComparisons.compare("  Wish you    WERE  herE    ", "wish you were here", 100))
    }
    @Test
    fun veryDecoratedStringsTest(){
        assert(100 == StringComparisons.compare("  Wish, you  ~  WERE-herE    ", "Wish you were here...", 100))
    }
    @Test
    fun accentedStrings(){
        assert(100 == StringComparisons.compare("àäầéèêẽ-íïőucç", "aaaeeee_iiouçĉ", 100))
    }
    @Test
    fun oneDifferenceStrings1(){
        assert(100 == StringComparisons.compare("rovge", "rouge", 100, 1))
        assert(100 == StringComparisons.compare("rougf ", "rouge", 100, 1))
        assert(100 == StringComparisons.compare("rovge", "rouge", 100, 1))
        assert(100 == StringComparisons.compare("  Wish, yoO  ~  WERE-herE    ", "Wish you were here...", 100, 1))
        assert(100 == StringComparisons.compare("àûầéèêẽ-íïöucç", "aaaeeee_iiouçĉ", 100, 1))
        }
    @Test
    fun oneDifferenceStrings2(){
        assert(100 == StringComparisons.compare("roge", "rouge", 100, 1))
        assert(100 == StringComparisons.compare("rouuge", "rouge", 100, 1))
        assert(100 == StringComparisons.compare("master of pupppets", "master of puppets", 100, 1))
        assert(100 == StringComparisons.compare("master of pupets", "master of puppets", 100, 1))
        assert(100 == StringComparisons.compare("master of puppet", "master of puppets", 100, 1))
    }
    @Test
    fun twoDifferencesStrings2(){
        assert(100 == StringComparisons.compare("rge", "rouge", 100, 2))
        assert(100 == StringComparisons.compare("rouuuge", "rouge", 100, 2))
        assert(100 == StringComparisons.compare("mastwr og puppets", "master of puppets", 100, 2))
        assert(100 == StringComparisons.compare("master of pupet", "master of puppets", 100, 2))
        assert(100 == StringComparisons.compare("master of puppppets", "master of puppets", 100, 2))
    }
    @Test
    fun differentStrings(){
        assert(0 == StringComparisons.compare("rge", "rouge", 100, 1))
        assert(0 == StringComparisons.compare("rougf", "rouge", 100, 0))
    }

}