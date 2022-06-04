package com.github.fribourgsdp.radio.mockimplementations

import com.github.fribourgsdp.radio.util.JSONParser
import com.github.fribourgsdp.radio.external.musixmatch.LyricsGetter
import com.github.fribourgsdp.radio.utils.testLyrics6
import okhttp3.OkHttpClient
import java.util.concurrent.CompletableFuture

object MockLyricsGetter : LyricsGetter {

    override fun getLyrics(
        songName: String,
        artistName: String,
        client: OkHttpClient,
        parser: JSONParser
    ): CompletableFuture<String> {
        return CompletableFuture.completedFuture(testLyrics6)
    }

    override fun getSongID(
        songName: String,
        artistName: String,
        client: OkHttpClient,
        parser: JSONParser
    ): CompletableFuture<Int> {
        throw NotImplementedError()
    }

    override fun markSongName(lyrics: String, name: String): String {
        TODO("Not yet implemented")
    }
}