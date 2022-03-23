package com.github.fribourgsdp.radio

import android.content.Intent
import androidx.test.espresso.Espresso
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import junit.framework.TestCase.assertEquals
import okhttp3.*
import org.hamcrest.Matchers
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import java.util.concurrent.ExecutionException

class ImportSpotifyPlaylistsActivityTest {

    @Test
    fun intentToUserProfileWorks() {
        val testMap = hashMapOf<String, String>()
        testMap["uid1"] = "playlist1"
        testMap["uid2"] = "playlist2"
        val testSet = mutableSetOf<Playlist>()
        testSet.add(Playlist("one", Genre.COUNTRY))
        val result = ImportSpotifyPlaylistsActivity.constructPlaylistIntent(Intent(), testMap, testSet)
        assert(result.hasExtra("nameToUid"))
        assert(result.hasExtra("playlists"))
        assert(result.hasExtra(COMING_FROM_SPOTIFY_ACTIVITY_FLAG))
    }

    @Test
    fun buildSpotifyRequestWorks(){
        val tokenTest = "123456"
        assert(
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
        assert(future.get().isEmpty())
    }

    @Test
    fun getPlaylistsWhenResponseHasErrorMessage(){
        val future = ImportSpotifyPlaylistsActivity.getUserPlaylists(MockPlaylistHTTPClient(), ErrorJSONParser())
        assert(future.get().isEmpty())
    }

    @Test
    fun getPlaylistsWithMockServerWorks(){
        val future = ImportSpotifyPlaylistsActivity.getUserPlaylists(MockPlaylistHTTPClient(), mockPlaylistJSONParser())
        assertEquals("1234", future.get()["80's classics"])
        assertEquals("8976", future.get()["rock music"])
    }

    @Test
    fun getPlaylistsContentWorks(){
        val future = ImportSpotifyPlaylistsActivity.getPlaylistContent("1234", MockPlaylistHTTPClient(), mockPlaylistContentJSONParser())
        val setValue = future.get()
        assert(setValue.contains(Song("Rouge", "Michel Sardou")))
        assert(setValue.contains(Song("Narcos", "Migos")))
    }

    @Test
    fun loadSongsToPlaylistsOnEmptyMapWorks(){
        val result = ImportSpotifyPlaylistsActivity.loadSongsToPlaylists(mutableMapOf())
        assert(result.isEmpty())
    }

    @Test(expected = ExecutionException::class)
    fun getPlaylistContentWithFaultyServerCrashes(){
        val future = ImportSpotifyPlaylistsActivity.getPlaylistContent("1234", FailingHTTPClient())
        println(future.get())
    }

    @Test
    fun getPlaylistContentWithNullParsedResponseFails(){
        val future = ImportSpotifyPlaylistsActivity.getPlaylistContent("1234", MockPlaylistHTTPClient(), NullJSONParser())
        assert(future.get().isEmpty())
    }

    @Test
    fun getPlaylistContentFailsIfJsonContainsError(){
        val future = ImportSpotifyPlaylistsActivity.getPlaylistContent("1224", MockPlaylistHTTPClient(), ErrorJSONParser())
        assert(future.get().isEmpty())
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

    private class mockPlaylistJSONParser : JSONParser(){
        override fun parse(s: String?): JSONObject? {
            return JSONObject()
                .put("items",
                    JSONArray()
                        .put(JSONObject().put("name", "80's classics").put("id", "1234"))
                        .put(JSONObject().put("name", "rock music").put("id", "8976")))
        }
    }

    private class mockPlaylistContentJSONParser : JSONParser(){
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