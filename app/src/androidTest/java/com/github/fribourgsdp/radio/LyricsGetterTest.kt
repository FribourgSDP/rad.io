package com.github.fribourgsdp.radio

import androidx.test.ext.junit.runners.AndroidJUnit4
import okhttp3.*
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import org.junit.Assert
import java.util.concurrent.ExecutionException


/**
 * Lyrics Getter Test
 */
@RunWith(AndroidJUnit4::class)
class LyricsGetterTest {
    @Test
    fun getLyricsFromSongAndArtist(){
        val f = LyricsGetter.getLyrics("rouge", "sardou", OkHttpClient())
        val lyrics = f.get()
        assert(lyrics.startsWith("Rouge\nComme un soleil couchant de Méditerranée"))
    }
    @Test
    fun emptyLyricsTest(){
        val lyricsFuture = LyricsGetter.getLyrics("stream", "dream theater", OkHttpClient())
        val lyrics : String = lyricsFuture.get()
        assert(lyrics.equals("---No lyrics were found for this song.---"))
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
        assert(lyrics.equals("---No lyrics were found for this song.---"))
    }
    @Test(expected = ExecutionException::class)
    fun getLyricsFailingHTTPClientTest(){
        val f = LyricsGetter.getLyrics("rouge", "sardou", SemiFailingHTTPClient())
        val lyrics = f.get()
    }
    @Test(expected = Exception::class)
    fun getSongIDFailingHTTPClientTest(){
        val id = LyricsGetter.getSongID("rouge", "sardou", FailingHTTPClient())
        id.get()
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

}