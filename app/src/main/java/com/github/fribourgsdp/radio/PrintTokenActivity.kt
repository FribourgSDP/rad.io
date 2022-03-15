package com.github.fribourgsdp.radio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.CompletableFuture

const val SPOTIFY_PLAYLIST_INFO_BASE_URL = "https://api.spotify.com/v1/playlists/"
const val SPOTIFY_GET_PLAYLIST_IDS_BASE_URL = "https://api.spotify.com/v1/me/playlists"
const val SPOTIFY_SONG_FILTER_NAME_ARTIST = "/tracks?fields=items(track(name%2C%20artists(name)))"
const val PLAYLIST_INFO_ERROR = "---An error occured while fetching the playlist information.---"
var TOKEN: String? = null

class PrintTokenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_print_token)
        val userToken = intent.getStringExtra("auth_token")
        TOKEN = userToken

        var playlistNames: String = String()
        val playlistMap: CompletableFuture<HashMap<String, String>> = getUserPlaylists()
        val map = playlistMap.get()
        for ((name, id) in map){
            println(name + ":" + getPlaylistContent(id).get())
            playlistNames += name + "\n"
        }
        val messageTextView: TextView = findViewById(R.id.playlistsTextView)
        messageTextView.text = playlistNames
    }

    private fun getPlaylistContent(playlistId: String, client: OkHttpClient = OkHttpClient(), parser : JSONParser = JSONStandardParser()) : CompletableFuture<String> {
        val future = CompletableFuture<String>()
        val auth: String? = "Bearer $TOKEN"
        val url = SPOTIFY_PLAYLIST_INFO_BASE_URL + playlistId + SPOTIFY_SONG_FILTER_NAME_ARTIST
        val request = Request.Builder().url(url)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", auth)
            .build()
        client.newCall(request).enqueue(GetPlaylistInfoCallback(future, parser))
        return future
    }

    private fun getUserPlaylists(client: OkHttpClient = OkHttpClient(), parser : JSONParser = JSONStandardParser()) : CompletableFuture<HashMap<String, String>> {
        val future = CompletableFuture<HashMap<String, String>>()
        val auth: String? = "Bearer $TOKEN"
        val request = Request.Builder().url(SPOTIFY_GET_PLAYLIST_IDS_BASE_URL)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", auth)
            .build()
        client.newCall(request).enqueue(GetPlaylistIdsCallback(future, parser))
        return future
    }

    private class GetPlaylistIdsCallback(private val future : CompletableFuture<HashMap<String, String>>, private val parser : JSONParser) :
        Callback {
        override fun onFailure(call: Call, e: IOException) {
            future.completeExceptionally(e)
        }

        override fun onResponse(call: Call, response: Response) {
            val parsedResponseString = response.body()?.string()
            val parsedResponse = parser.parse(parsedResponseString)
            val playlistNameToId = HashMap<String, String>()
            if (parsedResponse == null || parsedResponse.has("error")){
                //Case where request has failed.
            }
            else {
                val playlists = parsedResponse.getJSONArray("items")
                for (i in 0 until playlists.length()){
                    var playlist = playlists.getJSONObject(i)
                    playlistNameToId[playlist.getString("name")] = playlist.getString("id")
                    }
                }
            future.complete(playlistNameToId)
            }
        }


    private class GetPlaylistInfoCallback(private val future : CompletableFuture<String>, private val parser : JSONParser) :
        Callback {
        override fun onFailure(call: Call, e: IOException) {
            future.completeExceptionally(e)
        }

        override fun onResponse(call: Call, response: Response) {
            val parsedResponseString = response.body()?.string()
            val parsedResponse = parser.parse(parsedResponseString)
            var playlistInfo : String? = ""
            println(parsedResponse.toString())
            if (parsedResponse == null || parsedResponse.has("error")){
                playlistInfo = PLAYLIST_INFO_ERROR
            }
            else {
                val songs = parsedResponse.getJSONArray("items")
                for (i in 0 until songs.length()){
                    playlistInfo += songs.getJSONObject(i).getJSONObject("track").getJSONArray("artists").getJSONObject(0).getString("name") + " || " + songs.getJSONObject(i).getJSONObject("track").getString("name") + "\n"
                }
            }
            future.complete(playlistInfo)
        }
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