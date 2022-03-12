package com.github.fribourgsdp.radio

import okhttp3.*
import org.json.JSONException
import java.io.IOException
import java.util.concurrent.CompletableFuture
import org.json.JSONObject
import java.util.*

const val API_KEY = "a3454edb65483e706c127deaa11df69d"
const val BASE_URL = "http://api.musixmatch.com/ws/1.1/"
const val  LYRICS_NOT_FOUND = "---No lyrics were found for this song.---"

/**
 * Tool to get lyrics from a given song name and artist using Musixmatch API.
 * API Call doc : https://stackoverflow.com/questions/45219379/how-to-make-an-api-request-in-kotlin
 */

class LyricsGetter {
    companion object {

        // TODO: ASK VICTOR how to extract those strings
        //private val API_KEY = instance.getString(R.string.api_key)
        //private val BASE_URL = Resources.getSystem().getString(R.string.musixmatch_url)
        //private val LYRICS_NOT_FOUND = Resources.getSystem().getString(R.string.lyrics_not_found)

        /**
         * Asks Musixmatch and retrieves the lyrics of a song.
         * The song name and artist name can be empty or incomplete, the server can still find it.
         * @param songName The name of the queried song
         * @param artistName The name of the artist of this song
         * @param client The HTTP Client used for connection
         * @param parser The JSON parser used to parse response
         */
        fun getLyrics(
            songName: String,
            artistName: String,
            client: OkHttpClient = OkHttpClient(),
            parser: JSONParser = JSONStandardParser()
        ): CompletableFuture<String> {
            val future = CompletableFuture<String>()
            val trackIDFuture = getSongID(songName, artistName, client)
            val trackID: Int
            try {
                trackID = trackIDFuture.get()
            } catch (e: Throwable) {
                future.complete(LYRICS_NOT_FOUND)
                return future
            }
            val url =
                BASE_URL + "track.lyrics.get?track_id=" + trackID.toString() + "&apikey=" + API_KEY
            val request = Request.Builder().url(url).build()
            client.newCall(request).enqueue(GetLyricsCallback(future, parser))
            return future.thenApply { s -> markSongName(cleanLyrics(s), songName) }
        }

        /**
         * Asks Musixmatch to get the ID of a song and returns it.
         * @param songName The name of the queried song
         * @param artistName The name of the artist of this song
         * @param client The HTTP Client used for connection
         * @param parser The JSON parser used to parse response
         * @return The ID of the searched song
         */
        fun getSongID(songName: String, artistName: String, client: OkHttpClient = OkHttpClient(), parser : JSONParser = JSONStandardParser()) : CompletableFuture<Int> {
            //API needed : 24
            val future = CompletableFuture<Int>()
            val url =
                BASE_URL + "track.search?q_track=" + songName + "&q_artist=" + artistName + "&apikey=" + API_KEY + "&s_artist_rating=desc"
            val request = Request.Builder().url(url).build()
            client.newCall(request).enqueue(GetSongIDCallback(future, parser))
            return future
        }

        private class GetLyricsCallback(private val future : CompletableFuture<String>, private val parser : JSONParser) : Callback {
            override fun onFailure(call: Call, e: IOException) {
                future.completeExceptionally(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val parsedResponseString = response.body()?.string()
                val parsedResponse = parser.parse(parsedResponseString)
                var lyrics : String?
                if(parsedResponse == null){
                    lyrics = LYRICS_NOT_FOUND
                } else {
                    val status = parsedResponse.getJSONObject("message").getJSONObject("header").getInt("status_code")
                    if (status == 404) {
                        lyrics = LYRICS_NOT_FOUND
                    } else {
                        lyrics = parsedResponse
                            .getJSONObject("message")
                            .getJSONObject("body")
                            .getJSONObject("lyrics")
                            .getString("lyrics_body")
                    }
                    if (lyrics?.isEmpty() == true) {
                        lyrics = LYRICS_NOT_FOUND
                    }
                }
                future.complete(lyrics)
            }
        }

        private class GetSongIDCallback(private val future : CompletableFuture<Int>, private val parser : JSONParser) : Callback {
            override fun onFailure(call: Call, e: IOException) {
                future.completeExceptionally(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val parsedResponse = parser.parse(response.body()?.string())
                if(parsedResponse == null){
                    future.completeExceptionally(Exception("Error parsing response"))
                }
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
        }

        private fun cleanLyrics(lyrics : String) : String{
            if(lyrics == LYRICS_NOT_FOUND) {
                return lyrics
            }
            val allLines = lyrics.split("\n")
            val onlyLyricsLines = allLines.subList(0, allLines.size-4)
            val sj = StringJoiner("\n")
            onlyLyricsLines.forEach { e -> sj.add(e) }
            return sj.toString()
        }

        private fun markSongName(lyrics : String, name : String) : String{
            return lyrics.replace(name, "<em>${name[0].uppercase() + name.lowercase().substring(1)}</em>", ignoreCase = true)
        }

        abstract class JSONParser : JSONObject(){
            abstract fun parse(s : String?) : JSONObject?
        }
        
        class JSONStandardParser() : JSONParser() {
            override fun parse(s : String?) : JSONObject? {
                val out : JSONObject? = try{
                    s?.let { JSONObject(it) }
                } catch (e : JSONException){
                    null
                }
                return out
            }
        }
    }
}
