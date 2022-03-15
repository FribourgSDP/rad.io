package com.github.fribourgsdp.radio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.CompletableFuture

const val SPOTIFY_BASE_URL = "https://api.spotify.com/v1/playlists/20f4OMzTITWJiRJ1g7wcP7/tracks?fields=items(track(name%2C%20artists(name)))"
const val PLAYLIST_INFO_ERROR = "---An error occured while fetching the playlist information.---"
var TOKEN: String? = null

class PrintTokenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_print_token)
        val message = intent.getStringExtra("auth_token")
        val messageTextView: TextView = findViewById(R.id.tokenTextView)
        messageTextView.text = message
        TOKEN = message
        val futureResult: CompletableFuture<String> = getPlaylistContent()
        println(futureResult.get())
    }

    fun getPlaylistContent(client: OkHttpClient = OkHttpClient(), parser : JSONParser = JSONStandardParser()) : CompletableFuture<String> {
        val future = CompletableFuture<String>()
        val auth: String? = "Bearer $TOKEN"
        val request = Request.Builder().url(SPOTIFY_BASE_URL)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", auth).build()
        client.newCall(request).enqueue(GetPlaylistInfoCallback(future, parser))
        return future
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