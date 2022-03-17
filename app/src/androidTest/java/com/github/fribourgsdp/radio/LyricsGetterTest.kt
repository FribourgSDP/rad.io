package com.github.fribourgsdp.radio

import android.util.Log
import okhttp3.*
import org.json.JSONObject
import org.junit.Test
import org.junit.Assert.*
import java.io.IOException
import java.util.concurrent.ExecutionException

/**
 * Lyrics Getter Test
 */
//@RunWith(AndroidJUnit4::class)
class LyricsGetterTest {
    @Test
    fun getLyricsFromSongAndArtist(){
        val f = LyricsGetter.getLyrics("hurricane", "bob dylan", OkHttpClient())
        val lyrics = f.get()
        assertTrue(lyrics.startsWith("Pistol shots ring out in the barroom night"))
    }
    @Test
    fun emptyLyricsTest(){
        val lyricsFuture = LyricsGetter.getLyrics("stream", "dream theater", OkHttpClient())
        val lyrics : String = lyricsFuture.get()
        assertTrue(lyrics.equals("---No lyrics were found for this song.---"))
    }
    @Test(expected = Exception::class)
    fun songIDsongNotFoundTest(){
        val songIDFuture = LyricsGetter.getSongID("fsdgfdgdfgdfgdfg", "weoir hpfasdsfno", OkHttpClient())
        songIDFuture.get()
    }
    @Test
    fun songNotFoundTest(){
        val lyricsFuture = LyricsGetter.getLyrics("fsdgfdgdfgdfgdfg", "weoir hpfasdsfno", OkHttpClient())
        val lyrics = lyricsFuture.get()
        assertTrue(lyrics.equals("---No lyrics were found for this song.---"))
    }
    @Test(expected = ExecutionException::class)
    fun getLyricsFailingHTTPClientTest(){
        val f = LyricsGetter.getLyrics("rouge", "sardou", SemiFailingHTTPClient())
        val lyrics = f.get()
        println(lyrics)
    }
    @Test(expected = Exception::class)
    fun getSongIDFailingHTTPClientTest(){
        val id = LyricsGetter.getSongID("rouge", "sardou", FailingHTTPClient())
        val i = id.get()
        println(i)
    }
    @Test(expected = Exception::class)
    fun getSongIDMalformedResponse(){
        val id = LyricsGetter.getSongID("rouge", "sardou", OkHttpClient(), NullJSONParser())
        println(id.get())
    }
    @Test
    fun getLyricsMalformedResponse(){
        val lyrics = LyricsGetter.getLyrics("rouge", "sardou", OkHttpClient(), NullJSONParser()).get()
        assert(lyrics.equals("---No lyrics were found for this song.---"))
    }
    @Test
    fun emptyLyrics(){
        val lyrics = LyricsGetter.getLyrics("rouge", "sardou", OkHttpClient(), EmptyLyricsJSONParser()).get()
        assert(lyrics.equals("---No lyrics were found for this song.---"))
    }
    @Test
    fun lyricsGetterCreationTest(){
    	val LyricsGetter = LyricsGetter()
    }
    @Test
    fun jsonStandardParserDoesntThrowException(){
        val s = LyricsGetter.Companion.JSONStandardParser().parse("32q87rfha98 73298r7h08qwoehr703o490{{{{{")
        assertNull(s)
    }
    @Test
    fun cleanLyricsTest1(){
        val lyrics = LyricsGetter.getLyrics("rouge", "sardou").get()
        assertFalse(lyrics.contains("commercial"))
    }
    @Test
    fun emphasizeSongNameInLyrics(){
        val lyrics = LyricsGetter.markSongName(LyricsGetter.getLyrics("rouge", "sardou").get(), "rouge")
        assertTrue(lyrics.startsWith("<strike>Rouge</strike><br>Comme un soleil couchant de Méditerranée"))
    }

    class FailingHTTPClient : OkHttpClient() {
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
    class SemiFailingHTTPClient() : OkHttpClient() {
        override fun newCall(request: Request): Call {
            return SemiFailingCall(request)
        }
        class SemiFailingCall(val request : Request) : Call{

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
                if (t == 1){
                    t += 1
                    OkHttpClient().newCall(request).enqueue(responseCallback)
                } else {
                    responseCallback.onFailure(this, IOException("Failure !"))
                }
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

            companion object {
                var t = 1
            }
        }
    }
    class NullJSONParser() : LyricsGetter.Companion.JSONParser() {
        override fun parse(s: String?): JSONObject? {
            return null
        }
    }
    class EmptyLyricsJSONParser : LyricsGetter.Companion.JSONParser(){
        override fun parse(s: String?): JSONObject {
            return JSONObject("{\"message\":{\"header\":{\"status_code\":200,\"execute_time\":0.0089538097381592},\"body\":{\"lyrics\":{\"lyrics_id\":18905350,\"explicit\":1,\"lyrics_body\":\"\"}}}}")
        }
    }

}
