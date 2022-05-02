package com.github.fribourgsdp.radio

import android.util.Log
import okhttp3.*
import java.io.IOException
import java.util.*
import java.util.concurrent.CompletableFuture

const val API_KEY = "a3454edb65483e706c127deaa11df69d"
const val BASE_URL = "http://api.musixmatch.com/ws/1.1/"
const val TRACK_LYRICS_GET = "track.lyrics.get"
const val TRACK_ID_FIELD = "track_id"
const val API_KEY_FIELD = "apikey"
const val TRACK_SEARCH = "track.search"
const val QUERY_TRACK_FIELD = "q_track"
const val QUERY_ARTIST_FIELD = "q_artist"
const val SORT_CONDITION = "s_artist_rating=desc"

interface LyricsGetter{
    fun getLyrics(
        songName: String,
        artistName: String = "",
        client: OkHttpClient = OkHttpClient(),
        parser: JSONParser = JSONStandardParser()
    ): CompletableFuture<String>
    fun getSongID(songName: String, artistName: String, client: OkHttpClient = OkHttpClient(), parser : JSONParser = JSONStandardParser()) : CompletableFuture<Int>
}
/**
 * Tool to get lyrics from a given song name and artist using Musixmatch API.
 * API Call doc : https://stackoverflow.com/questions/45219379/how-to-make-an-api-request-in-kotlin
 */
object MusixmatchLyricsGetter : LyricsGetter {
    const val LYRICS_NOT_FOUND = "---No lyrics were found for this song.---"

    class NoLyricsFoundForThisSong : Exception()
    class BackendError(val nTries : Int = 0) : Exception()

    /**
     * Asks Musixmatch and retrieves the lyrics of a song.
     * The song name and artist name can be empty or incomplete, the server can still find it.
     * @param songName The name of the queried song
     * @param artistName The name of the artist of this song
     * @param client The HTTP Client used for connection
     * @param parser The JSON parser used to parse response
     */
    override fun getLyrics(
        songName: String,
        artistName: String,
        client: OkHttpClient,
        parser: JSONParser
    ): CompletableFuture<String> {
//            return CompletableFuture.completedFuture("If you feel little chance make a stance")
        Log.println(Log.ASSERT, "*", "LYRICS GETTER CALL !!!!")
        val future = CompletableFuture<String>()
        val trackIDFuture = getSongID(songName, artistName, client)
        val trackID: Int
        try {
            trackID = trackIDFuture.get()
        } catch (e: Throwable) {
            future.completeExceptionally(NoLyricsFoundForThisSong())
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
    override fun getSongID(songName: String, artistName: String, client: OkHttpClient, parser : JSONParser) : CompletableFuture<Int> {
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
                future.completeExceptionally(BackendError())
            } else {
                val status = parsedResponse.getJSONObject("message").getJSONObject("header").getInt("status_code")
                checkStatus(status, future) {
                    lyrics = parsedResponse
                        .getJSONObject("message")
                        .getJSONObject("body")
                        .getJSONObject("lyrics")
                        .getString("lyrics_body")
                    if (lyrics?.isEmpty() == true) {
                        future.completeExceptionally(NoLyricsFoundForThisSong())
                    } else {
                        future.complete(lyrics)
                    }
                }
            }
        }
    }

    private class GetSongIDCallback(private val future : CompletableFuture<Int>, private val parser : JSONParser) : Callback {
        override fun onFailure(call: Call, e: IOException) {
            future.completeExceptionally(e)
        }

        override fun onResponse(call: Call, response: Response) {
            val parsedResponse = parser.parse(response.body()?.string())
            if(parsedResponse == null){
                future.completeExceptionally(BackendError())
            } else{
                val status = parsedResponse.getJSONObject("message").getJSONObject("header").getInt("status_code")
                checkStatus(status, future) {
                    val trackList = parsedResponse
                        .getJSONObject("message")
                        .getJSONObject("body")
                        .getJSONArray("track_list")
                    if (trackList.length() == 0) {
                        future.completeExceptionally(NoLyricsFoundForThisSong())
                    } else {
                        val firstTrackID = trackList
                            .getJSONObject(0)
                            ?.getJSONObject("track")
                            ?.getInt("track_id")
                        future.complete(firstTrackID)
                    }
                }
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
        return if (name.isEmpty()){
            lyrics
        } else
            lyrics
                .replace(name, "<strike>${name[0].uppercase() + name.lowercase().substring(1)}</strike>", ignoreCase = true)
                .replace("\n", "<br>")
    }

    /**
     * If status is 404, it means this song will never have lyrics.
     * If status is not 404 but still of the form 4xx or 5xx, this request could have encountered an error, and another request could work
     * Else if no problem execute function.
     */
    private fun <T> checkStatus(
        status: Int,
        future: CompletableFuture<T>,
        function: () -> Boolean, ) {
        when {
            status == 404 -> {
                future.completeExceptionally(NoLyricsFoundForThisSong())
            }
            status >= 400 -> {
                future.completeExceptionally(BackendError())
            }
            else -> function.invoke()
        }
    }
}
