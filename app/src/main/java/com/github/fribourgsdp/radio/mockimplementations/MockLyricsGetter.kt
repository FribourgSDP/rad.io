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
        return CompletableFuture.completedFuture(
            "If you feel, little chance, make a stance\n" +
                "Looking for, better days, let me say\n" +
                    "Something's wrong, when you can't, let me go\n" +
                    "For to long, long, long...\n" +
                    "\n" +
                    "Momentum owns you\n" +
                    "Controlling her too")
    }

    override fun getSongID(
        songName: String,
        artistName: String,
        client: OkHttpClient,
        parser: JSONParser
    ): CompletableFuture<Int> {
        throw NotImplementedError()
    }
}