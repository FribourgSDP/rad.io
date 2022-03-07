package com.github.fribourgsdp.radio

import okhttp3.*
import java.io.IOException
import java.util.concurrent.CompletableFuture
import org.json.JSONObject

/**
 * Tool to get lyrics from a given song name and artist using Musixmatch API.
 * API Call doc : https://stackoverflow.com/questions/45219379/how-to-make-an-api-request-in-kotlin
 */

private const val API_KEY = "a3454edb65483e706c127deaa11df69d"
private const val BASE_URL = "http://api.musixmatch.com/ws/1.1/"



class LyricsGetter {

    companion object {

        private val client = OkHttpClient()
        fun getLyrics(songName: String, artistName: String) : CompletableFuture<String> {
            val future = CompletableFuture<String>()
            val trackIDFuture = getSongID(songName, artistName)
            val trackID : Int
            try {
                trackID = trackIDFuture.get()
            } catch (e : Throwable){
                future.completeExceptionally(e)
                return future
            }
            val url = BASE_URL + "track.lyrics.get?track_id=" + trackID + "&apikey=" + API_KEY
            val request = Request.Builder().url(url).build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    future.completeExceptionally(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val parsedResponse = response.body()?.string()?.let {JSONObject(it)}
                    // TODO: assert not null ?
                    val status = parsedResponse?.getJSONObject("message")?.getJSONObject("header")?.getInt("status_code")
                    val lyrics : String?
                    if (status == 404){
                        lyrics = "---No lyrics were found for this song.---"
                    } else {
                        lyrics = parsedResponse
                            ?.getJSONObject("message")
                            ?.getJSONObject("body")
                            ?.getJSONObject("lyrics")
                            ?.getString("lyrics_body")
                    }
                    future.complete(lyrics)
                }
            })
            return future
        }

        fun getSongID(songName: String, artistName: String) : CompletableFuture<Int> {
            // TODO: I HAD to change API level from 23 to 24
            val future = CompletableFuture<Int>()
            val url =
                BASE_URL + "track.search?q_track=" + songName + "&q_artist=" + artistName + "&apikey=" + API_KEY + "&s_artist_rating=desc"
            val request = Request.Builder().url(url).build()
            client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    future.completeExceptionally(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val parsedResponse = response.body()?.string()?.let { JSONObject(it) } // TODO: wtf
                    val trackList = parsedResponse
                        ?.getJSONObject("message")
                        ?.getJSONObject("body")
                        ?.getJSONArray("track_list")
                    if(trackList?.length() == 0){
                        future.completeExceptionally(Exception("Song not found"))
                    } else {
                        val firstTrackID = trackList
                            ?.getJSONObject(0)
                            ?.getJSONObject("track")
                            ?.getInt("track_id")
                        future.complete(firstTrackID)
                    }
                }
            })
            return future
        }
    }
}