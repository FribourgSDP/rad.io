package com.github.fribourgsdp.radio

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith

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
}