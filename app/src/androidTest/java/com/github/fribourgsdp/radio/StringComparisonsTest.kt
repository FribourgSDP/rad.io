package com.github.fribourgsdp.radio

import com.github.fribourgsdp.radio.backend.StringComparisons
import org.junit.Test

class StringComparisonsTest {
    @Test
    fun equalStringsTest(){
        assert(0 == StringComparisons.compare("rouge", "rouge"))
    }
    @Test
    fun spacefulStringsTest(){
        assert(0 == StringComparisons.compare("  wish you    were  here    ", "wish you were here"))
    }
    @Test
    fun differentCasesStringsTest(){
        assert(0 == StringComparisons.compare("  Wish you    WERE  herE    ", "wish you were here"))
    }
    @Test
    fun veryDecoratedStringsTest(){
        assert(0 == StringComparisons.compare("  Wish, you  ~  WERE-herE    ", "Wish you were here..."))
    }
    @Test
    fun accentedStrings(){
        assert(0 == StringComparisons.compare("àäầéèêẽ-íïőucç", "aaaeeee_iiouçĉ"))
    }
    @Test
    fun oneDifferenceStrings1(){
        assert(1 == StringComparisons.compare("rovge", "rouge"))
        assert(1 == StringComparisons.compare("rougf ", "rouge"))
        assert(1 == StringComparisons.compare("rovge", "rouge"))
        assert(1 == StringComparisons.compare("  Wish, yoO  ~  WERE-herE    ", "Wish you were here..."))
        assert(1 == StringComparisons.compare("àûầéèêẽ-íïöucç", "aaaeeee_iiouçĉ"))
        }
    @Test
    fun oneDifferenceStrings2(){
        assert(1 == StringComparisons.compare("roge", "rouge"))
        assert(1 == StringComparisons.compare("rouuge", "rouge"))
        assert(1 == StringComparisons.compare("master of pupppets", "master of puppets"))
        assert(1 == StringComparisons.compare("master of pupets", "master of puppets"))
        assert(1 == StringComparisons.compare("master of puppet", "master of puppets"))
    }
    @Test
    fun twoDifferencesStrings2(){
        assert(2 == StringComparisons.compare("rge", "rouge"))
        assert(2 == StringComparisons.compare("rouuuge", "rouge"))
        assert(2 == StringComparisons.compare("mastwr og puppets", "master of puppets"))
        assert(2 == StringComparisons.compare("master of pupet", "master of puppets"))
        assert(2 == StringComparisons.compare("master of puppppets", "master of puppets"))
    }
    @Test
    fun differentStrings(){
        assert(1 != StringComparisons.compare("rge", "rouge"))
        assert(0 != StringComparisons.compare("rougf", "rouge"))
    }

}