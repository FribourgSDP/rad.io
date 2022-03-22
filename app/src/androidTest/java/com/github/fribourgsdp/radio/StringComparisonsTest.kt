package com.github.fribourgsdp.radio

import org.junit.Test

class StringComparisonsTest {
    @Test
    fun equalStringsTest(){
        assert(StringComparisons.compare("rouge", "rouge", 100))
    }
    @Test
    fun spacefulStringsTest(){
        assert(StringComparisons.compare("  wish you    were  here    ", "wish you were here", 100))
    }
    @Test
    fun differentCasesStringsTest(){
        assert(StringComparisons.compare("  Wish you    WERE  herE    ", "wish you were here", 100))
    }
    @Test
    fun veryDecoratedStringsTest(){
        assert(StringComparisons.compare("  Wish, you  ~  WERE-herE    ", "Wish you were here...", 100))
    }
    @Test
    fun accentedStrings(){
        assert(StringComparisons.compare("àäầéèêẽ-íïőucç", "aaaeeee_iiouçĉ", 100))
    }
    @Test
    fun oneDifferenceStrings1(){
        assert(StringComparisons.compare("rovge", "rouge", 1))
        assert(StringComparisons.compare("rougf ", "rouge", 1))
        assert(StringComparisons.compare("rovge", "rouge", 1))
        assert(StringComparisons.compare("  Wish, yoO  ~  WERE-herE    ", "Wish you were here...", 1))
        assert(StringComparisons.compare("àûầéèêẽ-íïöucç", "aaaeeee_iiouçĉ", 1))
        }
    @Test
    fun oneDifferenceStrings2(){
        assert(StringComparisons.compare("roge", "rouge", 1))
        assert(StringComparisons.compare("rouuge", "rouge", 1))
        assert(StringComparisons.compare("master of pupppets", "master of puppets", 1))
        assert(StringComparisons.compare("master of pupets", "master of puppets", 1))
        assert(StringComparisons.compare("master of puppet", "master of puppets", 1))
    }
    @Test
    fun twoDifferencesStrings2(){
        assert(StringComparisons.compare("rge", "rouge", 2))
        assert(StringComparisons.compare("rouuuge", "rouge", 2))
        assert(StringComparisons.compare("mastwr og puppets", "master of puppets", 2))
        assert(StringComparisons.compare("master of pupet", "master of puppets", 2))
        assert(StringComparisons.compare("master of puppppets", "master of puppets", 2))
    }
    @Test
    fun differentStrings(){
        assert(!StringComparisons.compare("rge", "rouge", 1))
        assert(!StringComparisons.compare("rougf", "rouge", 0))
    }
    @Test
    fun compareAndGetPointsTest(){
        assert(100 == StringComparisons.compareAndGetPoints("comfortably numb", "comfortably numb"))
        assert(90 == StringComparisons.compareAndGetPoints("comfortably num", "comfortably numb"))
        assert(80 == StringComparisons.compareAndGetPoints("confortably num", "comfortably numb"))
    }

}