package com.github.fribourgsdp.radio

import junit.framework.TestCase.assertEquals
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test
import java.io.IOException
import java.util.concurrent.ExecutionException

class PrintTokenActivityTest {
    @Test
    fun playlistNamesForEmptyPlaylist(){
        assertEquals("No Spotify playlists were found for this user.", PrintTokenActivity.getPlaylistNames(
            HashMap()
        ))
    }

    @Test
    fun playlistNamesForAUserWithPlaylists(){
        assert(PrintTokenActivity.getPlaylistNames(hashMapOf("classical music" to "abcid123", "football evening" to "123456playlistid")).contains("classical music"))
        assert(PrintTokenActivity.getPlaylistNames(hashMapOf("classical music" to "abcid123", "football evening" to "123456playlistid")).contains("football evening"))

    }

    @Test
    fun buildGetPlaylistsRequestWorks(){
        val tokenTest = "123456"
        assert(
            Request.Builder().url(SPOTIFY_GET_PLAYLIST_IDS_BASE_URL)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $tokenTest")
            .build().url() == PrintTokenActivity.buildGetPlaylistsRequest(tokenTest).url())
    }

    @Test
    fun buildGetPlaylistContentRequestWorks(){
        val tokenTest = "123456"
        val playlistId = "uniqueId"
        assert(Request.Builder().url(SPOTIFY_PLAYLIST_INFO_BASE_URL + playlistId + SPOTIFY_SONG_FILTER_NAME_ARTIST)
            .addHeader("Accept", "application/json")
            .addHeader("Content-Type", "application/json")
            .addHeader("Authorization", "Bearer $tokenTest")
            .build().url() == PrintTokenActivity.buildGetPlaylistContentRequest(playlistId, tokenTest).url())
    }

    @Test(expected = ExecutionException::class)
    fun getPlaylistsFailingHttpClient(){
        val future = PrintTokenActivity.getUserPlaylists(FailingHTTPClient(), PrintTokenActivity.Companion.JSONStandardParser())
        println(future.get())
    }

    @Test
    fun getPlaylistsFailingParser(){
        val future = PrintTokenActivity.getUserPlaylists(MockPlaylistHTTPClient(), NullJSONParser())
        assert(future.get().isEmpty())
    }

    @Test
    fun getPlaylistsWhenResponseHasErrorMessage(){
        val future = PrintTokenActivity.getUserPlaylists(MockPlaylistHTTPClient(), ErrorJSONParser())
        assert(future.get().isEmpty())
    }

    @Test
    fun getPlaylistsWithMockServerWorks(){
       val future = PrintTokenActivity.getUserPlaylists(MockPlaylistHTTPClient(), mockPlaylistJSONParser())
       assertEquals("1234", future.get()["80's classics"])
        assertEquals("8976", future.get()["rock music"])
    }

    @Test
    fun getPlaylistsContentWorks(){
        val future = PrintTokenActivity.getPlaylistContent("1234", MockPlaylistHTTPClient(), mockPlaylistContentJSONParser())
        val stringValue = future.get()
        assert(stringValue.contains("Narcos"))
        assert(future.get().contains("Rouge"))
        assert(future.get().contains("Migos"))
        assert(future.get().contains("Michel Sardou"))

    }

    @Test(expected = ExecutionException::class)
    fun getPlaylistContentWithFaultyServerCrashes(){
        val future = PrintTokenActivity.getPlaylistContent("1234", FailingHTTPClient())
        println(future.get())
    }

    @Test
    fun getPlaylistContentWithNullParsedResponseFails(){
        val future = PrintTokenActivity.getPlaylistContent("1234", MockPlaylistHTTPClient(), NullJSONParser())
        assertEquals(PLAYLIST_INFO_ERROR, future.get())
    }

    @Test
    fun getPlaylistContentFailsIfJsonContainsError(){
        val future = PrintTokenActivity.getPlaylistContent("1224", MockPlaylistHTTPClient(), ErrorJSONParser())
        assertEquals(PLAYLIST_INFO_ERROR, future.get())

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
    private class NullJSONParser() : PrintTokenActivity.Companion.JSONParser() {
        override fun parse(s: String?): JSONObject? {
            return null
        }
    }

    private class ErrorJSONParser : PrintTokenActivity.Companion.JSONParser(){
        override fun parse(s: String?): JSONObject {
            return JSONObject("{\"error\": \"bla\"}")
        }
    }

    private class mockPlaylistJSONParser : PrintTokenActivity.Companion.JSONParser(){
        override fun parse(s: String?): JSONObject? {
            return JSONObject()
                .put("items",
                    JSONArray()
                        .put(JSONObject().put("name", "80's classics").put("id", "1234"))
                        .put(JSONObject().put("name", "rock music").put("id", "8976")))
        }
    }

    private class mockPlaylistContentJSONParser : PrintTokenActivity.Companion.JSONParser(){
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