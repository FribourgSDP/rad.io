package com.github.fribourgsdp.radio

import android.util.Log
import okhttp3.*
import org.json.JSONException
import java.io.IOException
import java.util.concurrent.CompletableFuture
import org.json.JSONObject
import java.util.*

const val API_KEY = "a3454edb65483e706c127deaa11df69d"
const val BASE_URL = "http://api.musixmatch.com/ws/1.1/"
const val TRACK_LYRICS_GET = "track.lyrics.get"
const val TRACK_ID_FIELD = "track_id"
const val API_KEY_FIELD = "apikey"
const val TRACK_SEARCH = "track.search"
const val QUERY_TRACK_FIELD = "q_track"
const val QUERY_ARTIST_FIELD = "q_artist"
const val SORT_CONDITION = "s_artist_rating=desc"
/**
 * Tool to get lyrics from a given song name and artist using Musixmatch API.
 * API Call doc : https://stackoverflow.com/questions/45219379/how-to-make-an-api-request-in-kotlin
 */

class LyricsGetter {
    companion object {
        const val LYRICS_NOT_FOUND = "---No lyrics were found for this song.---"

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
            artistName: String = "",
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
            val url = "$BASE_URL$TRACK_LYRICS_GET?$TRACK_ID_FIELD=$trackID&$API_KEY_FIELD=$API_KEY"

            val request = Request.Builder().url(url).build()
            client.newCall(request).enqueue(GetLyricsCallback(future, parser))
            return future.thenApply { s -> cleanLyrics(s) }
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
            val url = "$BASE_URL$TRACK_SEARCH?$QUERY_TRACK_FIELD=$songName&$QUERY_ARTIST_FIELD=$artistName&$API_KEY_FIELD=$API_KEY&$SORT_CONDITION"

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

        /**
         * Transforms the lyrics by removing its 4 last lines. Indeed, the lyrics we get from Musixmatch contain a useless mention "These lyrics are not for commercial use, ..." .
         * The mention to Musixmatch will be displayed elsewhere, like in the activity displaying the lyrics.
         */
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

        /**
         * Translates lyrics to HTML format crossing out the name of the song.
         * @param lyrics The lyrics to transform
         * @param name The name of the song to cross
         */
        fun markSongName(lyrics : String, name : String) : String{
            return lyrics
                .replace(name, "<strike>${name[0].uppercase() + name.lowercase().substring(1)}</strike>", ignoreCase = true)
                .replace("\n", "<br>")
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
