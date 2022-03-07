package com.github.fribourgsdp.radio

import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Exception


/**
 * Lyrics Getter Test
 */
@RunWith(AndroidJUnit4::class)
class LyricsGetterTest {
    @Test
    fun getLyricsFromSongAndArtist(){
        val f = LyricsGetter.getLyrics("rouge", "sardou")
        val lyrics = f.get()
        assert(lyrics.startsWith("Rouge\nComme un soleil couchant de Méditerranée"))
    }
    @Test
    fun emptyLyricsTest(){
        val lyrics = LyricsGetter.getLyrics("stream", "dream theater").get()
        assert(lyrics.equals("---No lyrics were found for this song.---"))
    }
    @Test(expected = Exception::class)
    fun songNotFoundTest(){
        val songIDFuture = LyricsGetter.getSongID("fsdgfdgdfgdfgdfg", "weoir hpfasdsfno")
        songIDFuture.get()
    }

}