package com.github.fribourgsdp.radio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import okhttp3.*
import java.io.IOException
import java.util.concurrent.CompletableFuture

const val SPOTIFY_PLAYLIST_INFO_BASE_URL = "https://api.spotify.com/v1/playlists/"
const val SPOTIFY_GET_PLAYLIST_IDS_BASE_URL = "https://api.spotify.com/v1/me/playlists"
const val SPOTIFY_SONG_FILTER_NAME_ARTIST = "/tracks?fields=items(track(name%2C%20artists(name)))"
const val PLAYLIST_INFO_ERROR = "---An error occured while fetching the playlist information.---"

var TOKEN: String? = null

class ImportSpotifyPlaylistsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import_spotify_playlists)
        val userToken = intent.getStringExtra("auth_token")
        TOKEN = userToken

        val playlistMap: CompletableFuture<HashMap<String, String>> = getUserPlaylists()
        val playlistsNameToUid = playlistMap.get()
        val playlists = loadSongsToPlaylists(playlistsNameToUid)

        var intent = Intent(this@ImportSpotifyPlaylistsActivity, UserProfileActivity::class.java)
        intent = constructPlaylistIntent(intent, playlistsNameToUid, playlists)
        startActivity(intent)
    }


    companion object {

        fun constructPlaylistIntent(intent: Intent, playlistNameToUId: HashMap<String, String>, playlists: Set<Playlist>): Intent {
            intent.putExtra("nameToUid", playlistNameToUId)
            intent.putExtra("playlists", playlists.toHashSet())
            intent.putExtra(COMING_FROM_SPOTIFY_ACTIVITY_FLAG, true)
            return intent
        }

        fun getPlaylistContent(playlistId: String, client: OkHttpClient = OkHttpClient(), parser : JSONParser = JSONStandardParser()) : CompletableFuture<Set<Song>> {
            val future = CompletableFuture<Set<Song>>()
            val request = buildSpotifyRequest(SPOTIFY_PLAYLIST_INFO_BASE_URL + playlistId + SPOTIFY_SONG_FILTER_NAME_ARTIST, TOKEN)
            client.newCall(request).enqueue(GetPlaylistInfoCallback(future, parser))
            return future
        }

        fun loadSongsToPlaylists(playlistNameToId: Map<String, String>, client: OkHttpClient = OkHttpClient(), parser : JSONParser = JSONStandardParser()): Set<Playlist> {
            val playlists = mutableSetOf<Playlist>()
            for ((name, id) in playlistNameToId) {
                var newPlaylist = Playlist(name)
                val futurePlaylist = getPlaylistContent(id, client, parser)
                val playlistSongs = futurePlaylist.get()
                if (playlistSongs.isNotEmpty()){
                    newPlaylist.addSongs(playlistSongs)
                    playlists.add(newPlaylist)
                }
            }
            return playlists
        }

        fun getUserPlaylists(client: OkHttpClient = OkHttpClient(), parser : JSONParser = JSONStandardParser()) : CompletableFuture<HashMap<String, String>> {
            val future = CompletableFuture<HashMap<String, String>>()
            val request = buildSpotifyRequest(SPOTIFY_GET_PLAYLIST_IDS_BASE_URL, TOKEN)
            client.newCall(request).enqueue(GetPlaylistIdsCallback(future, parser))
            return future
        }

        fun buildSpotifyRequest(url: String, user_token: String?): Request{
            return Request.Builder().url(url)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $user_token")
                .build()
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
                    future.complete(playlistNameToId)
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


        private class GetPlaylistInfoCallback(private val future : CompletableFuture<Set<Song>>, private val parser : JSONParser) :
            Callback {
            override fun onFailure(call: Call, e: IOException) {
                future.completeExceptionally(e)
            }

            override fun onResponse(call: Call, response: Response) {
                val parsedResponseString = response.body()?.string()
                val parsedResponse = parser.parse(parsedResponseString)
                var playlistInfo = mutableSetOf<Song>()
                println(parsedResponse.toString())
                if (parsedResponse == null || parsedResponse.has("error")){

                }
                else {
                    val songs = parsedResponse.getJSONArray("items")
                    for (i in 0 until songs.length()){
                        var songObject = songs.getJSONObject(i).getJSONObject("track")
                        var artists = ""
                        var songArtists = songObject.getJSONArray("artists")
                        for(j in 0 until songArtists.length()){
                            artists += songArtists.getJSONObject(j).getString("name") + ", "
                        }
                        artists = artists.substring(0, artists.length - 2)
                        playlistInfo.add(Song(songs.getJSONObject(i).getJSONObject("track").getString("name"), artists))
                    }
                }
                future.complete(playlistInfo)
            }
        }
    }

}