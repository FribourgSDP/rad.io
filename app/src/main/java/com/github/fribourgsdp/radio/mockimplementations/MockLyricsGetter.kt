package com.github.fribourgsdp.radio.mockimplementations

import com.github.fribourgsdp.radio.JSONParser
import com.github.fribourgsdp.radio.LyricsGetter
import okhttp3.OkHttpClient
import java.util.concurrent.CompletableFuture

object MockLyricsGetter : LyricsGetter{
    override fun getLyrics(
        songName: String,
        artistName: String,
        client: OkHttpClient,
        parser: JSONParser
    ): CompletableFuture<String> {
        TODO("Not yet implemented")
    }

    override fun getSongID(
        songName: String,
        artistName: String,
        client: OkHttpClient,
        parser: JSONParser
    ): CompletableFuture<Int> {
        TODO("Not yet implemented")
    }
}