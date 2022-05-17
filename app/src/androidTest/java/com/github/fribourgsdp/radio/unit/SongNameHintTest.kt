package com.github.fribourgsdp.radio.unit

import android.util.Log
import com.github.fribourgsdp.radio.config.Settings
import com.github.fribourgsdp.radio.config.language.Language
import com.github.fribourgsdp.radio.external.google.GoogleSignInActivity
import com.github.fribourgsdp.radio.util.SongNameHint
import org.junit.Assert
import org.junit.Test

class SongNameHintTest {

    @Test
    fun getCorrectNonAccentString(){
        val songNameHint = SongNameHint("")
        val s = songNameHint.stripAccents("J'ai mangé des pommes à l'ananas en août.!? Et ça m'a plu. ")
        Assert.assertEquals("J'ai mange des pommes a l'ananas en aout.!? Et ca m'a plu. ", s)
    }

    @Test
    fun replaceLetterByLine(){
        val songNameHint = SongNameHint("")
        val s = songNameHint.replaceWithLine("J'ai 23 ans de plus que Batman!")
        Assert.assertEquals("_'__ __ ___ __ ____ ___ ______!", s)
    }

    @Test
    fun getIndexFromLine(){
        val songNameHint = SongNameHint("")
        val list = songNameHint.indexOfLetter("__ ' ___!..._")
        Assert.assertEquals(listOf(0,1,5,6,7,12), list)
    }

    @Test
    fun addCorrectlyOneLetter(){
        val songNameHint = SongNameHint(".....A.....")
        Assert.assertEquals("....._.....", songNameHint.toString())
        songNameHint.addALetter()
        Assert.assertEquals(".....A.....", songNameHint.toString())
    }


    @Test
    fun addCorrectlyManyLetter(){
        val songNameHint = SongNameHint("ABCDEFG")
        Assert.assertEquals(7, songNameHint.toString().filter { it == '_' }.count())
        songNameHint.addALetter()
        songNameHint.addALetter()
        Assert.assertEquals(5, songNameHint.toString().filter { it == '_' }.count())
        songNameHint.addALetter()
        songNameHint.addALetter()
        songNameHint.addALetter()
        songNameHint.addALetter()
        Assert.assertEquals(1, songNameHint.toString().filter { it == '_' }.count())
        songNameHint.addALetter()
        Assert.assertEquals(0, songNameHint.toString().filter { it == '_' }.count())
        songNameHint.addALetter()
        Assert.assertEquals(0, songNameHint.toString().filter { it == '_' }.count())
    }



}