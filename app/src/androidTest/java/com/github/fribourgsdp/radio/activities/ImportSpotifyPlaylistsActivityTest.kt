package com.github.fribourgsdp.radio.activities

import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.external.spotify.ImportSpotifyPlaylistsActivity
import com.github.fribourgsdp.radio.external.spotify.SPOTIFY_GET_PLAYLIST_IDS_BASE_URL
import com.github.fribourgsdp.radio.util.JSONParser
import com.github.fribourgsdp.radio.util.JSONStandardParser
import junit.framework.TestCase.assertEquals
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test
import java.io.IOException
import java.util.concurrent.ExecutionException
import org.junit.Assert.*

class ImportSpotifyPlaylistsActivityTest {

    @Test
    fun buildSpotifyRequestWorks(){
        val tokenTest = "123456"
        assertTrue(
            Request.Builder().url(SPOTIFY_GET_PLAYLIST_IDS_BASE_URL)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("Authorization", "Bearer $tokenTest")
                .build().url() == ImportSpotifyPlaylistsActivity.buildSpotifyRequest(
                SPOTIFY_GET_PLAYLIST_IDS_BASE_URL, tokenTest).url())
    }


    @Test(expected = ExecutionException::class)
    fun getPlaylistsFailingHttpClient(){
        val future = ImportSpotifyPlaylistsActivity.getUserPlaylists(FailingHTTPClient(), JSONStandardParser())
        println(future.get())
    }

    @Test
    fun getPlaylistsFailingParser(){
        val future = ImportSpotifyPlaylistsActivity.getUserPlaylists(MockPlaylistHTTPClient(), NullJSONParser())
        assertTrue(future.get().isEmpty())
    }

    @Test
    fun getPlaylistsWhenResponseHasErrorMessage(){
        val future = ImportSpotifyPlaylistsActivity.getUserPlaylists(MockPlaylistHTTPClient(), ErrorJSONParser2())
        assertTrue(future.get().isEmpty())
    }

    @Test
    fun malformedJSONResponseTest(){
        val future = ImportSpotifyPlaylistsActivity.getUserPlaylists(MockPlaylistHTTPClient())
    }

    @Test
    fun getPlaylistsWithMockServerWorks(){
        val future = ImportSpotifyPlaylistsActivity.getUserPlaylists(MockPlaylistHTTPClient(), MockPlaylistJSONParser())
        assertEquals("1234", future.get()["80's classics"])
        assertEquals("8976", future.get()["rock music"])
    }

    @Test
    fun getPlaylistsContentWorks(){
        val future = ImportSpotifyPlaylistsActivity.getPlaylistContent("1234", MockPlaylistHTTPClient(), MockPlaylistContentJSONParser())
        val setValue = future.get()
        assertTrue(setValue.contains(Song("Rouge", "Michel Sardou")))
        assertTrue(setValue.contains(Song("Narcos", "Migos")))
    }

    @Test
    fun loadSongsToPlaylistsOnEmptyMapWorks(){
        val result = ImportSpotifyPlaylistsActivity.loadSongsToPlaylists(mutableMapOf())
        assertTrue(result.isEmpty())
    }

    @Test(expected = ExecutionException::class)
    fun getPlaylistContentWithFaultyServerCrashes(){
        val future = ImportSpotifyPlaylistsActivity.getPlaylistContent("1234", FailingHTTPClient())
        println(future.get())
    }

    @Test
    fun getPlaylistContentWithNullParsedResponseFails(){
        val future = ImportSpotifyPlaylistsActivity.getPlaylistContent("1234", MockPlaylistHTTPClient(), NullJSONParser())
        assertTrue(future.get().isEmpty())
    }

    @Test
    fun getPlaylistContentFailsIfJsonContainsError(){
        val future = ImportSpotifyPlaylistsActivity.getPlaylistContent("1224", MockPlaylistHTTPClient(), ErrorJSONParser())
        assertTrue(future.get().isEmpty())
    }

    @Test
    fun loadSongsToPlaylistWorksProperly(){
        val map = mutableMapOf<String, String>()
        map["bla"] = "bli"
        val playlists = ImportSpotifyPlaylistsActivity.loadSongsToPlaylists(map, MockPlaylistHTTPClient(), MockPlaylistContentJSONParser())
        assertEquals(1, playlists.size)
        assertEquals("bla",playlists.last().name)
        assertTrue(playlists.last().getSongs().contains(Song("Narcos", "Migos", "")))
    }



    private class FailingHTTPClient : OkHttpClient() {
        override fun newCall(request: Request): Call {
            return FailingCall()
        }

        class FailingCall : Call {
            override fun clone(): Call {
                throw Exception()
            }

            override fun request(): Request {
                throw Exception()
            }

            override fun execute(): Response {
                throw Exception()
            }

            override fun enqueue(responseCallback: Callback) {
                responseCallback.onFailure(this, IOException("Failure !"))
            }

            override fun cancel() {
                throw Exception()
            }

            override fun isExecuted(): Boolean {
                throw Exception()
            }

            override fun isCanceled(): Boolean {
                throw Exception()
            }
        }
    }



    private class MockPlaylistHTTPClient : OkHttpClient() {
        override fun newCall(request: Request): Call {
            return SuccessfullCall()
        }

        class SuccessfullCall : Call {
            override fun clone(): Call {
                throw Exception()

            }

            override fun request(): Request {
                throw Exception()
            }

            override fun execute(): Response {
                throw Exception()
            }

            override fun enqueue(responseCallback: Callback) {
                responseCallback.onResponse(this, Response.Builder().message("hihi").code(0).protocol(Protocol.HTTP_1_0).request(Request.Builder().url("https://publicobject.com/helloworld.txt").build()).build())

            }

            override fun cancel() {
                throw Exception()
            }

            override fun isExecuted(): Boolean {
                throw Exception()
            }

            override fun isCanceled(): Boolean {
                throw Exception()
            }
        }
    }
    private class NullJSONParser() : JSONParser() {
        override fun parse(s: String?): JSONObject? {
            return null
        }
    }

    private class ErrorJSONParser : JSONParser(){
        override fun parse(s: String?): JSONObject {
            return JSONObject("{\"error\": \"bla\"}")
        }
    }
    private class ErrorJSONParser2 : JSONParser(){
        override fun parse(s: String?): JSONObject {
            return JSONObject("{\"error\": \"bla\"}")
        }
    }

    private class MockPlaylistJSONParser : JSONParser(){
        override fun parse(s: String?): JSONObject? {
            return JSONObject()
                .put("items",
                    JSONArray()
                        .put(JSONObject().put("name", "80's classics").put("id", "1234"))
                        .put(JSONObject().put("name", "rock music").put("id", "8976")))
        }
    }

    private class MockPlaylistContentJSONParser : JSONParser(){
        override fun parse(s: String?): JSONObject? {
            return JSONObject()
                .put("items",
                    JSONArray()
                        .put(JSONObject()
                            .put("track", JSONObject()
                                .put("name", "Narcos")
                                .put("artists", JSONArray().put(JSONObject().put("name", "Migos")))))
                        .put(JSONObject()
                            .put("track", JSONObject()
                                .put("name", "Rouge")
                                .put("artists", JSONArray().put(JSONObject().put("name", "Michel Sardou"))))))

        }
    }


}