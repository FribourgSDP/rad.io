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
        val f = LyricsGetter.getLyrics("hurricane", "bob dylan", MockOkHttpClient())
        val lyrics = f.get()
        assertTrue("actual : $lyrics",lyrics.startsWith("Pistol shots ring out in the barroom night"))
    }
    @Test
    fun emptyLyricsTest(){
        val lyricsFuture = LyricsGetter.getLyrics("stream", "dream theater", MockOkHttpClient())
        val lyrics : String = lyricsFuture.get()
        assertTrue(lyrics == "---No lyrics were found for this song.---")
    }
    @Test(expected = Exception::class)
    fun songIDsongNotFoundTest(){
        val songIDFuture = LyricsGetter.getSongID("fsdgfdgdfgdfgdfg", "weoir hpfasdsfno", MockOkHttpClient())
        songIDFuture.get()
    }
    @Test
    fun songNotFoundTest(){
        val lyricsFuture = LyricsGetter.getLyrics("fsdgfdgdfgdfgdfg", "weoir hpfasdsfno", MockOkHttpClient())
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
        val id = LyricsGetter.getSongID("rouge", "sardou", MockOkHttpClient(), NullJSONParser())
        println(id.get())
    }
    @Test
    fun getLyricsMalformedResponse(){
        val lyrics = LyricsGetter.getLyrics("rouge", "sardou", MockOkHttpClient(), NullJSONParser()).get()
        assert(lyrics.equals("---No lyrics were found for this song.---"))
    }
    @Test
    fun emptyLyrics(){
        val lyrics = LyricsGetter.getLyrics("rouge", "sardou", MockOkHttpClient(), EmptyLyricsJSONParser()).get()
        assert(lyrics.equals("---No lyrics were found for this song.---"))
    }
    @Test
    fun lyricsGetterCreationTest(){
    	val LyricsGetter = LyricsGetter()
    }
    @Test

    fun JSONStandardParserDoesntThrowException(){
        val s = JSONStandardParser().parse("32q87rfha98 73298r7h08qwoehr703o490{{{{{")
        assertNull(s)
    }
    @Test
    fun cleanLyricsTest1(){
        val lyrics = LyricsGetter.getLyrics("rouge", "sardou").get()
        assertFalse(lyrics.contains("commercial"))
    }
    @Test
    fun emphasizeSongNameInLyrics(){
        val lyrics = LyricsGetter.markSongName(LyricsGetter.getLyrics("rouge", "sardou", MockOkHttpClient()).get(), "rouge")
        assertTrue("actual : $lyrics",lyrics.startsWith("<strike>Rouge</strike><br>Comme un soleil couchant de Méditerranée"))
    }

    class MockOkHttpClient : OkHttpClient(){
        override fun newCall(request1: Request): Call {
            return MockCall(request1)
        }
        class MockCall(private val req: Request) : Call{
            override fun clone(): Call {
                throw Exception()
            }

            override fun request(): Request {
                throw Exception()
            }

            override fun execute(): Response {
                throw Exception()
            }

            override fun enqueue(rc: Callback) {
                Log.println(Log.ASSERT, "****", req.toString())
                val s = if(req.toString() == "Request{method=GET, url=http://api.musixmatch.com/ws/1.1/track.search?q_track=hurricane&q_artist=bob%20dylan&apikey=a3454edb65483e706c127deaa11df69d&s_artist_rating=desc, tag=null}"){
                    "{\"message\":{\"header\":{\"status_code\":200,\"execute_time\":0.081793069839478,\"available\":11},\"body\":{\"track_list\":[{\"track\":{\"track_id\":115119412,\"track_name\":\"Hurricane\",\"track_name_translation_list\":[],\"track_rating\":4,\"commontrack_id\":47560177,\"instrumental\":0,\"explicit\":0,\"has_lyrics\":0,\"has_subtitles\":0,\"has_richsync\":0,\"num_favourite\":0,\"album_id\":20913096,\"album_name\":\"Highway 61 Revisited - A Tribute To Bob Dylan\",\"artist_id\":28930361,\"artist_name\":\"Highway 61 Revisited - Bob Dylan\",\"track_share_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Highway-61-Revisited-Bob-Dylan\\/Hurricane?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"track_edit_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Highway-61-Revisited-Bob-Dylan\\/Hurricane\\/edit?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"restricted\":0,\"updated_time\":\"2015-12-10T08:57:13Z\",\"primary_genres\":{\"music_genre_list\":[]}}},{\"track\":{\"track_id\":115119412,\"track_name\":\"Hurricane\",\"track_name_translation_list\":[],\"track_rating\":25,\"commontrack_id\":2317,\"instrumental\":0,\"explicit\":1,\"has_lyrics\":1,\"has_subtitles\":1,\"has_richsync\":1,\"num_favourite\":573,\"album_id\":24006067,\"album_name\":\"The Essential (2014 Revised)\",\"artist_id\":13,\"artist_name\":\"Bob Dylan\",\"track_share_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Bob-Dylan\\/Hurricane?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"track_edit_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Bob-Dylan\\/Hurricane\\/edit?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"restricted\":0,\"updated_time\":\"2019-11-11T10:51:21Z\",\"primary_genres\":{\"music_genre_list\":[{\"music_genre\":{\"music_genre_id\":1289,\"music_genre_parent_id\":34,\"music_genre_name\":\"Folk\",\"music_genre_name_extended\":\"Folk\",\"music_genre_vanity\":\"Folk\"}},{\"music_genre\":{\"music_genre_id\":1065,\"music_genre_parent_id\":10,\"music_genre_name\":\"Folk-Rock\",\"music_genre_name_extended\":\"Singer\\/Songwriter \\/ Folk-Rock\",\"music_genre_vanity\":\"Singer-Songwriter-Folk-Rock\"}}]}}},{\"track\":{\"track_id\":212448599,\"track_name\":\"Hurricane: The Story of Rubin Carter\",\"track_name_translation_list\":[],\"track_rating\":1,\"commontrack_id\":124418769,\"instrumental\":0,\"explicit\":0,\"has_lyrics\":0,\"has_subtitles\":0,\"has_richsync\":0,\"num_favourite\":0,\"album_id\":43408247,\"album_name\":\"Hurricane: The Story of Rubin Carter\",\"artist_id\":48438137,\"artist_name\":\"Bob Dylan Experience\",\"track_share_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Bob-Dylan-Experience\\/Hurricane-The-Story-of-Rubin-Carter?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"track_edit_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Bob-Dylan-Experience\\/Hurricane-The-Story-of-Rubin-Carter\\/edit?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"restricted\":0,\"updated_time\":\"2021-03-31T13:49:49Z\",\"primary_genres\":{\"music_genre_list\":[{\"music_genre\":{\"music_genre_id\":34,\"music_genre_parent_id\":0,\"music_genre_name\":\"Music\",\"music_genre_name_extended\":\"Music\",\"music_genre_vanity\":\"Music\"}}]}}},{\"track\":{\"track_id\":169230359,\"track_name\":\"Hurricane (Seacrest Motel Rehearsals)\",\"track_name_translation_list\":[],\"track_rating\":1,\"commontrack_id\":95050374,\"instrumental\":0,\"explicit\":0,\"has_lyrics\":0,\"has_subtitles\":0,\"has_richsync\":0,\"num_favourite\":0,\"album_id\":32252440,\"album_name\":\"Travelin' Thru, 1967 - 1969: The Bootleg Series, Vol. 15\",\"artist_id\":13,\"artist_name\":\"Bob Dylan\",\"track_share_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Bob-Dylan\\/Hurricane-Seacrest-Motel-Rehearsals?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"track_edit_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Bob-Dylan\\/Hurricane-Seacrest-Motel-Rehearsals\\/edit?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"restricted\":0,\"updated_time\":\"2019-05-01T03:10:04Z\",\"primary_genres\":{\"music_genre_list\":[{\"music_genre\":{\"music_genre_id\":34,\"music_genre_parent_id\":0,\"music_genre_name\":\"Music\",\"music_genre_name_extended\":\"Music\",\"music_genre_vanity\":\"Music\"}},{\"music_genre\":{\"music_genre_id\":21,\"music_genre_parent_id\":34,\"music_genre_name\":\"Rock\",\"music_genre_name_extended\":\"Rock\",\"music_genre_vanity\":\"Rock\"}}]}}},{\"track\":{\"track_id\":169230474,\"track_name\":\"It Takes a Lot to Laugh, It Takes a Train to Cry (Live at The Night of the Hurricane, Madison Square Garden, New York, NY - December 1975)\",\"track_name_translation_list\":[],\"track_rating\":1,\"commontrack_id\":95050479,\"instrumental\":0,\"explicit\":0,\"has_lyrics\":1,\"has_subtitles\":0,\"has_richsync\":0,\"num_favourite\":0,\"album_id\":32252440,\"album_name\":\"Travelin' Thru, 1967 - 1969: The Bootleg Series, Vol. 15\",\"artist_id\":13,\"artist_name\":\"Bob Dylan\",\"track_share_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Bob-Dylan\\/It-Takes-a-Lot-to-Laugh-It-Takes-a-Train-to-Cry-Live-at-The-Night-of-the-Hurricane-Madison-Square-Garden-New-York-NY-December-1975?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"track_edit_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Bob-Dylan\\/It-Takes-a-Lot-to-Laugh-It-Takes-a-Train-to-Cry-Live-at-The-Night-of-the-Hurricane-Madison-Square-Garden-New-York-NY-December-1975\\/edit?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"restricted\":0,\"updated_time\":\"2019-05-01T03:20:03Z\",\"primary_genres\":{\"music_genre_list\":[{\"music_genre\":{\"music_genre_id\":34,\"music_genre_parent_id\":0,\"music_genre_name\":\"Music\",\"music_genre_name_extended\":\"Music\",\"music_genre_vanity\":\"Music\"}},{\"music_genre\":{\"music_genre_id\":21,\"music_genre_parent_id\":34,\"music_genre_name\":\"Rock\",\"music_genre_name_extended\":\"Rock\",\"music_genre_vanity\":\"Rock\"}}]}}},{\"track\":{\"track_id\":169230395,\"track_name\":\"Hurricane (Live at Harvard Square Theatre, Cambridge, MA - November 1975)\",\"track_name_translation_list\":[],\"track_rating\":1,\"commontrack_id\":95050405,\"instrumental\":0,\"explicit\":1,\"has_lyrics\":1,\"has_subtitles\":0,\"has_richsync\":0,\"num_favourite\":0,\"album_id\":32252440,\"album_name\":\"Travelin' Thru, 1967 - 1969: The Bootleg Series, Vol. 15\",\"artist_id\":13,\"artist_name\":\"Bob Dylan\",\"track_share_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Bob-Dylan\\/Hurricane-Live-at-Harvard-Square-Theatre-Cambridge-MA-November-1975?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"track_edit_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Bob-Dylan\\/Hurricane-Live-at-Harvard-Square-Theatre-Cambridge-MA-November-1975\\/edit?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"restricted\":0,\"updated_time\":\"2019-05-01T03:22:16Z\",\"primary_genres\":{\"music_genre_list\":[{\"music_genre\":{\"music_genre_id\":34,\"music_genre_parent_id\":0,\"music_genre_name\":\"Music\",\"music_genre_name_extended\":\"Music\",\"music_genre_vanity\":\"Music\"}},{\"music_genre\":{\"music_genre_id\":21,\"music_genre_parent_id\":34,\"music_genre_name\":\"Rock\",\"music_genre_name_extended\":\"Rock\",\"music_genre_vanity\":\"Rock\"}}]}}},{\"track\":{\"track_id\":169230414,\"track_name\":\"Hurricane (Live at Boston Music Hall, Boston, MA - November 21, 1975 - Afternoon)\",\"track_name_translation_list\":[],\"track_rating\":1,\"commontrack_id\":95050423,\"instrumental\":0,\"explicit\":1,\"has_lyrics\":1,\"has_subtitles\":0,\"has_richsync\":0,\"num_favourite\":0,\"album_id\":32252440,\"album_name\":\"Travelin' Thru, 1967 - 1969: The Bootleg Series, Vol. 15\",\"artist_id\":13,\"artist_name\":\"Bob Dylan\",\"track_share_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Bob-Dylan\\/Hurricane-Live-at-Boston-Music-Hall-Boston-MA-November-21-1975-Afternoon?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"track_edit_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Bob-Dylan\\/Hurricane-Live-at-Boston-Music-Hall-Boston-MA-November-21-1975-Afternoon\\/edit?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"restricted\":0,\"updated_time\":\"2019-05-01T03:22:12Z\",\"primary_genres\":{\"music_genre_list\":[{\"music_genre\":{\"music_genre_id\":34,\"music_genre_parent_id\":0,\"music_genre_name\":\"Music\",\"music_genre_name_extended\":\"Music\",\"music_genre_vanity\":\"Music\"}},{\"music_genre\":{\"music_genre_id\":21,\"music_genre_parent_id\":34,\"music_genre_name\":\"Rock\",\"music_genre_name_extended\":\"Rock\",\"music_genre_vanity\":\"Rock\"}}]}}},{\"track\":{\"track_id\":172698311,\"track_name\":\"Hurricane - Live at Boston Music Hall, Boston, MA - November 21, 1975 - Evening\",\"track_name_translation_list\":[],\"track_rating\":24,\"commontrack_id\":95050443,\"instrumental\":0,\"explicit\":1,\"has_lyrics\":1,\"has_subtitles\":0,\"has_richsync\":0,\"num_favourite\":0,\"album_id\":32840460,\"album_name\":\"The Rolling Thunder Revue: The 1975 Live Recordings (Sampler)\",\"artist_id\":13,\"artist_name\":\"Bob Dylan\",\"track_share_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Bob-Dylan\\/Hurricane-Live-at-Boston-Music-Hall-Boston-MA-November-21-1975-Evening?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"track_edit_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Bob-Dylan\\/Hurricane-Live-at-Boston-Music-Hall-Boston-MA-November-21-1975-Evening\\/edit?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"restricted\":0,\"updated_time\":\"2021-03-18T11:03:09Z\",\"primary_genres\":{\"music_genre_list\":[{\"music_genre\":{\"music_genre_id\":1065,\"music_genre_parent_id\":10,\"music_genre_name\":\"Folk-Rock\",\"music_genre_name_extended\":\"Singer\\/Songwriter \\/ Folk-Rock\",\"music_genre_vanity\":\"Singer-Songwriter-Folk-Rock\"}},{\"music_genre\":{\"music_genre_id\":34,\"music_genre_parent_id\":0,\"music_genre_name\":\"Music\",\"music_genre_name_extended\":\"Music\",\"music_genre_vanity\":\"Music\"}},{\"music_genre\":{\"music_genre_id\":21,\"music_genre_parent_id\":34,\"music_genre_name\":\"Rock\",\"music_genre_name_extended\":\"Rock\",\"music_genre_vanity\":\"Rock\"}}]}}},{\"track\":{\"track_id\":169230457,\"track_name\":\"Hurricane (Live at Montreal Forum, Montreal, Quebec - December 1975)\",\"track_name_translation_list\":[],\"track_rating\":24,\"commontrack_id\":95050462,\"instrumental\":0,\"explicit\":1,\"has_lyrics\":1,\"has_subtitles\":0,\"has_richsync\":0,\"num_favourite\":0,\"album_id\":32252440,\"album_name\":\"Travelin' Thru, 1967 - 1969: The Bootleg Series, Vol. 15\",\"artist_id\":13,\"artist_name\":\"Bob Dylan\",\"track_share_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Bob-Dylan\\/Hurricane-Live-at-Montreal-Forum-Montreal-Quebec-December-1975?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"track_edit_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Bob-Dylan\\/Hurricane-Live-at-Montreal-Forum-Montreal-Quebec-December-1975\\/edit?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"restricted\":0,\"updated_time\":\"2019-05-01T03:22:05Z\",\"primary_genres\":{\"music_genre_list\":[{\"music_genre\":{\"music_genre_id\":34,\"music_genre_parent_id\":0,\"music_genre_name\":\"Music\",\"music_genre_name_extended\":\"Music\",\"music_genre_vanity\":\"Music\"}},{\"music_genre\":{\"music_genre_id\":21,\"music_genre_parent_id\":34,\"music_genre_name\":\"Rock\",\"music_genre_name_extended\":\"Rock\",\"music_genre_vanity\":\"Rock\"}}]}}},{\"track\":{\"track_id\":86724560,\"track_name\":\"Hurricane - Live at Memorial Auditorium, Worcester, MA - November 1975\",\"track_name_translation_list\":[],\"track_rating\":24,\"commontrack_id\":14399029,\"instrumental\":0,\"explicit\":1,\"has_lyrics\":1,\"has_subtitles\":1,\"has_richsync\":0,\"num_favourite\":0,\"album_id\":21057711,\"album_name\":\"The Bootleg Series, Vol. 5 - Bob Dylan Live 1975: The Rolling Thunder Revue\",\"artist_id\":13,\"artist_name\":\"Bob Dylan\",\"track_share_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Bob-Dylan\\/Hurricane-Live?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"track_edit_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Bob-Dylan\\/Hurricane-Live\\/edit?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"restricted\":0,\"updated_time\":\"2013-11-28T18:04:50Z\",\"primary_genres\":{\"music_genre_list\":[{\"music_genre\":{\"music_genre_id\":14,\"music_genre_parent_id\":34,\"music_genre_name\":\"Pop\",\"music_genre_name_extended\":\"Pop\",\"music_genre_vanity\":\"Pop\"}},{\"music_genre\":{\"music_genre_id\":21,\"music_genre_parent_id\":34,\"music_genre_name\":\"Rock\",\"music_genre_name_extended\":\"Rock\",\"music_genre_vanity\":\"Rock\"}},{\"music_genre\":{\"music_genre_id\":34,\"music_genre_parent_id\":0,\"music_genre_name\":\"Music\",\"music_genre_name_extended\":\"Music\",\"music_genre_vanity\":\"Music\"}}]}}}]}}}"
                } else if(req.toString() == "Request{method=GET, url=http://api.musixmatch.com/ws/1.1/track.lyrics.get?track_id=115119412&apikey=a3454edb65483e706c127deaa11df69d, tag=null}") {
                    "{\"message\":{\"header\":{\"status_code\":200,\"execute_time\":0.018281936645508},\"body\":{\"lyrics\":{\"lyrics_id\":19943678,\"explicit\":1,\"lyrics_body\":\"Pistol shots ring out in the barroom night\\nEnter Patty Valentine from the upper hall\\nShe sees the bartender in a pool of blood\\nCries out, \\\"My God, they've killed them all!\\\"\\n\\nHere comes the story of the Hurricane\\nThe man the authorities came to blame\\nFor somethin' that he never done\\nPut in a prison cell, but one time he could-a been\\nThe champion of the world\\n\\nThree bodies lyin' there does Patty see\\nAnd another man named Bello, movin' around mysteriously\\n\\\"I didn't do it,\\\" he says, and he throws up his hands\\n\\\"I was only robbin' the register, I hope you understand\\\"\\n\\n\\\"I saw them leavin',\\\" he says, and he stops\\n\\\"One of us had better call up the cops\\\"\\nAnd so Patty calls the cops\\nAnd they arrive on the scene\\nWith their red lights flashin' in the hot New Jersey night\\n\\nMeanwhile, far away in another part of town\\nRubin Carter and a couple of friends are drivin' around\\nNumber one contender for the middleweight crown\\nHad no idea what kinda shit was about to go down\\n\\nWhen a cop pulled him over to the side of the road\\nJust like the time before and the time before that\\nIn Paterson that's just the way things go\\nIf you're black you might as well not show up on the street\\n'Less you wanna draw the heat\\n\\nAlfred Bello had a partner and he had a rap for the cops\\nHim and Arthur Dexter Bradley were just out prowlin' around\\nHe said, \\\"I saw two men runnin' out, they looked like middleweights\\nThey jumped into a white car with out-of-state plates\\\"\\n\\nAnd Miss Patty Valentine just nodded her head\\nCop said, \\\"Wait a minute, boys, this one's not dead\\\"\\n...\\n\\n******* This Lyrics is NOT for Commercial use *******\\n(1409622437882)\",\"script_tracking_url\":\"https:\\/\\/tracking.musixmatch.com\\/t1.0\\/m_js\\/e_1\\/sn_0\\/l_19943678\\/su_0\\/rs_0\\/tr_3vUCANivONl3oTDOk8tT8WcS-l1x_nLhXI5IKFopLSFS_I_nZk1ZuaSm-SCIu_-NYGuNpiiqY7o7uSh2Hm0rUXMi7M2Dy6Fq5m16NayjWYeH1gxoohxflLjxoqHWwX_laPmUyTd0TjW05eYT7uMLTZ0LIgMQUWDsG4kQ5YTB1rD2slq3eap8dh9HzlFZvn7vTJ4ZnhprPjx1T8Dm8jdrBLg373yja4M75w4ezqsU1WCwXqQuLMWr2Rd3IvOOJ65jbk98ZNgbXVF2VteDon0OseO4MBZiEg_lUxYoEi7GtDQLjbJ_icSSn0srN8EltIg2T6gjbThCQm2D5wmQIWoOQHphpJZtlzJ4CRDnID2Ul98c0B7WjkGYD0lJbOA6OToMasYmacGpRlM8ho2b0AkCiQmivdvm4XJ_liUixXjN3FbsmqMUXEjvzBbRfQ\\/\",\"pixel_tracking_url\":\"https:\\/\\/tracking.musixmatch.com\\/t1.0\\/m_img\\/e_1\\/sn_0\\/l_19943678\\/su_0\\/rs_0\\/tr_3vUCAPsjTWkP___s4DCdF7cXWBjMt4pnjBHjKJqtZlCG7ida2IJaAMJduA-eMPFxz_3htwn-OSF0s1ISOgnWf-8pLn5P42TgrUclM6xKOwgxkp9GdYfr81w7Gm5eqPwT5Hdraq5ikVwcxZgYyCI32DiqLxX2O-2Gy2cIReYXuLopug6a160mV89LcwRdx48j-mjaUSEIYzH3tnnx4U0tIqgplYdywHs9uiBfyhsvXcJiDFZ_nQbRFB66QTTZs7ECyfoplF_QI9Dzupe0kHvrF1C9VmMRjgNnf3qorT3U_uPz8uY7LXtO-ukAkz8ODo6WT_TjBhCRTiBcxvvWemdWh4eY9-yZZy6GCV6TpdVN6CgnYBQgumqBdeUmZOWO4uGKXET-GAW3mKMVUM_PFp0inodaoifl7996wWHvMNkxpU1NOet4Hmp8dtYxyw\\/\",\"lyrics_copyright\":\"Lyrics powered by www.musixmatch.com. This Lyrics is NOT for Commercial use and only 30% of the lyrics are returned.\",\"updated_time\":\"2021-11-10T13:14:23Z\"}}}}"
                } else if(req.toString() == "Request{method=GET, url=http://api.musixmatch.com/ws/1.1/track.search?q_track=stream&q_artist=dream%20theater&apikey=a3454edb65483e706c127deaa11df69d&s_artist_rating=desc, tag=null}"){
                    "{\"message\":{\"header\":{\"status_code\":200,\"execute_time\":0.052242994308472,\"available\":6},\"body\":{\"track_list\":[{\"track\":{\"track_id\":37042055,\"track_name\":\"Stream of Consciousness (Instrumental)\",\"track_name_translation_list\":[],\"track_rating\":26,\"commontrack_id\":15388235,\"instrumental\":1,\"explicit\":0,\"has_lyrics\":0,\"has_subtitles\":0,\"has_richsync\":0,\"num_favourite\":6,\"album_id\":15713336,\"album_name\":\"Train of Thought\",\"artist_id\":75924,\"artist_name\":\"Dream Theater\",\"track_share_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Dream-Theater\\/Stream-of-Consciousness-Instrumental?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"track_edit_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Dream-Theater\\/Stream-of-Consciousness-Instrumental\\/edit?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"restricted\":0,\"updated_time\":\"2013-12-23T04:21:17Z\",\"primary_genres\":{\"music_genre_list\":[{\"music_genre\":{\"music_genre_id\":21,\"music_genre_parent_id\":34,\"music_genre_name\":\"Rock\",\"music_genre_name_extended\":\"Rock\",\"music_genre_vanity\":\"Rock\"}}]}}},{\"track\":{\"track_id\":219126498,\"track_name\":\"Stream of Consciousness - Instrumental Demo 2003\",\"track_name_translation_list\":[],\"track_rating\":23,\"commontrack_id\":129886378,\"instrumental\":0,\"explicit\":1,\"has_lyrics\":1,\"has_subtitles\":1,\"has_richsync\":0,\"num_favourite\":0,\"album_id\":45434898,\"album_name\":\"Lost Not Forgotten Archives: Train of Thought Instrumental Demos (2003)\",\"artist_id\":75924,\"artist_name\":\"Dream Theater\",\"track_share_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Dream-Theater\\/Stream-of-Consciousness-Instrumental-Demo-2003?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"track_edit_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Dream-Theater\\/Stream-of-Consciousness-Instrumental-Demo-2003\\/edit?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"restricted\":0,\"updated_time\":\"2021-12-06T15:41:01Z\",\"primary_genres\":{\"music_genre_list\":[{\"music_genre\":{\"music_genre_id\":21,\"music_genre_parent_id\":34,\"music_genre_name\":\"Rock\",\"music_genre_name_extended\":\"Rock\",\"music_genre_vanity\":\"Rock\"}}]}}},{\"track\":{\"track_id\":37041871,\"track_name\":\"Stream of Consciousness (Live)\",\"track_name_translation_list\":[],\"track_rating\":1,\"commontrack_id\":15388140,\"instrumental\":0,\"explicit\":0,\"has_lyrics\":0,\"has_subtitles\":0,\"has_richsync\":0,\"num_favourite\":0,\"album_id\":15713318,\"album_name\":\"Live At Budokan\",\"artist_id\":75924,\"artist_name\":\"Dream Theater\",\"track_share_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Dream-Theater\\/Stream-of-Consciousness-Live?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"track_edit_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Dream-Theater\\/Stream-of-Consciousness-Live\\/edit?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"restricted\":0,\"updated_time\":\"2013-12-23T04:11:11Z\",\"primary_genres\":{\"music_genre_list\":[{\"music_genre\":{\"music_genre_id\":21,\"music_genre_parent_id\":34,\"music_genre_name\":\"Rock\",\"music_genre_name_extended\":\"Rock\",\"music_genre_vanity\":\"Rock\"}}]}}},{\"track\":{\"track_id\":86253064,\"track_name\":\"Stream Of Consciousness - Live At Budokan\",\"track_name_translation_list\":[],\"track_rating\":23,\"commontrack_id\":48552215,\"instrumental\":1,\"explicit\":0,\"has_lyrics\":0,\"has_subtitles\":0,\"has_richsync\":0,\"num_favourite\":0,\"album_id\":21026229,\"album_name\":\"Live At Budokan (US Release)\",\"artist_id\":75924,\"artist_name\":\"Dream Theater\",\"track_share_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Dream-Theater\\/Stream-Of-Consciousness-Live-At-Budokan?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"track_edit_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Dream-Theater\\/Stream-Of-Consciousness-Live-At-Budokan\\/edit?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"restricted\":0,\"updated_time\":\"2015-12-30T15:00:36Z\",\"primary_genres\":{\"music_genre_list\":[]}}},{\"track\":{\"track_id\":130544987,\"track_name\":\"Stream Of Consciousness [Instrumental] (2009 Remastered Album Version)\",\"track_name_translation_list\":[],\"track_rating\":21,\"commontrack_id\":72742368,\"instrumental\":1,\"explicit\":0,\"has_lyrics\":0,\"has_subtitles\":0,\"has_richsync\":0,\"num_favourite\":0,\"album_id\":26225983,\"album_name\":\"Train of Thought\",\"artist_id\":75924,\"artist_name\":\"Dream Theater\",\"track_share_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Dream-Theater\\/Stream-Of-Consciousness-Instrumental-2009-Remastered-Album-Version?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"track_edit_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Dream-Theater\\/Stream-Of-Consciousness-Instrumental-2009-Remastered-Album-Version\\/edit?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"restricted\":0,\"updated_time\":\"2017-06-04T17:40:01Z\",\"primary_genres\":{\"music_genre_list\":[]}}},{\"track\":{\"track_id\":31086241,\"track_name\":\"Stream Of Consciousness\",\"track_name_translation_list\":[],\"track_rating\":23,\"commontrack_id\":3479220,\"instrumental\":1,\"explicit\":0,\"has_lyrics\":0,\"has_subtitles\":0,\"has_richsync\":0,\"num_favourite\":114,\"album_id\":13807391,\"album_name\":\"Train of Thought\",\"artist_id\":75924,\"artist_name\":\"Dream Theater\",\"track_share_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Dream-Theater\\/Stream-of-Consciousness?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"track_edit_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Dream-Theater\\/Stream-of-Consciousness\\/edit?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"restricted\":0,\"updated_time\":\"2018-11-07T13:50:08Z\",\"primary_genres\":{\"music_genre_list\":[{\"music_genre\":{\"music_genre_id\":21,\"music_genre_parent_id\":34,\"music_genre_name\":\"Rock\",\"music_genre_name_extended\":\"Rock\",\"music_genre_vanity\":\"Rock\"}}]}}}]}}}"
                } else if(req.toString() == "Request{method=GET, url=http://api.musixmatch.com/ws/1.1/track.lyrics.get?track_id=37042055&apikey=a3454edb65483e706c127deaa11df69d, tag=null}"){
                    "{\"message\":{\"header\":{\"status_code\":404,\"execute_time\":0.011165142059326},\"body\":[]}}"
                }else if(req.toString() == "Request{method=GET, url=http://api.musixmatch.com/ws/1.1/track.search?q_track=fsdgfdgdfgdfgdfg&q_artist=weoir%20hpfasdsfno&apikey=a3454edb65483e706c127deaa11df69d&s_artist_rating=desc, tag=null}"){
                    "{\"message\":{\"header\":{\"status_code\":200,\"execute_time\":0.01686692237854,\"available\":0},\"body\":{\"track_list\":[]}}}"
                }else if(req.toString() == "Request{method=GET, url=http://api.musixmatch.com/ws/1.1/track.search?q_track=rouge&q_artist=sardou&apikey=a3454edb65483e706c127deaa11df69d&s_artist_rating=desc, tag=null}"){
                    "{\"message\":{\"header\":{\"status_code\":200,\"execute_time\":0.026069164276123,\"available\":8},\"body\":{\"track_list\":[{\"track\":{\"track_id\":39648585,\"track_name\":\"Rouge\",\"track_name_translation_list\":[],\"track_rating\":16,\"commontrack_id\":2178050,\"instrumental\":0,\"explicit\":1,\"has_lyrics\":1,\"has_subtitles\":1,\"has_richsync\":0,\"num_favourite\":10,\"album_id\":15876670,\"album_name\":\"Rouge\",\"artist_id\":25616,\"artist_name\":\"Michel Sardou\",\"track_share_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Michel-Sardou\\/Rouge?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"track_edit_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Michel-Sardou\\/Rouge\\/edit?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"restricted\":0,\"updated_time\":\"2019-03-11T09:55:40Z\",\"primary_genres\":{\"music_genre_list\":[{\"music_genre\":{\"music_genre_id\":50000064,\"music_genre_parent_id\":34,\"music_genre_name\":\"French Pop\",\"music_genre_name_extended\":\"French Pop\",\"music_genre_vanity\":\"French-Pop\"}}]}}},{\"track\":{\"track_id\":39654034,\"track_name\":\"Rouge (Karaok\\u00e9)\",\"track_name_translation_list\":[],\"track_rating\":1,\"commontrack_id\":16265587,\"instrumental\":0,\"explicit\":0,\"has_lyrics\":0,\"has_subtitles\":0,\"has_richsync\":0,\"num_favourite\":0,\"album_id\":15876992,\"album_name\":\"Karaok\\u00e9 Michel Sardou\",\"artist_id\":25616,\"artist_name\":\"Michel Sardou\",\"track_share_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Michel-Sardou\\/Rouge-Karaok%C3%A9?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"track_edit_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Michel-Sardou\\/Rouge-Karaok%C3%A9\\/edit?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"restricted\":0,\"updated_time\":\"2013-12-29T21:36:29Z\",\"primary_genres\":{\"music_genre_list\":[{\"music_genre\":{\"music_genre_id\":52,\"music_genre_parent_id\":34,\"music_genre_name\":\"Karaoke\",\"music_genre_name_extended\":\"Karaoke\",\"music_genre_vanity\":\"Karaoke\"}}]}}},{\"track\":{\"track_id\":213966274,\"track_name\":\"Rouge - Live au Palais des Congr\\u00e8s \\/ 1987\",\"track_name_translation_list\":[],\"track_rating\":17,\"commontrack_id\":125499872,\"instrumental\":0,\"explicit\":0,\"has_lyrics\":0,\"has_subtitles\":0,\"has_richsync\":0,\"num_favourite\":0,\"album_id\":43828388,\"album_name\":\"Le concert de sa vie\",\"artist_id\":25616,\"artist_name\":\"Michel Sardou\",\"track_share_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Michel-Sardou\\/Rouge-Live-au-Palais-des-Congr%C3%A8s-1987?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"track_edit_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Michel-Sardou\\/Rouge-Live-au-Palais-des-Congr%C3%A8s-1987\\/edit?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"restricted\":0,\"updated_time\":\"2021-04-30T16:08:39Z\",\"primary_genres\":{\"music_genre_list\":[{\"music_genre\":{\"music_genre_id\":34,\"music_genre_parent_id\":0,\"music_genre_name\":\"Music\",\"music_genre_name_extended\":\"Music\",\"music_genre_vanity\":\"Music\"}}]}}},{\"track\":{\"track_id\":84817256,\"track_name\":\"Rouge - Live A L'Olympia 2013\",\"track_name_translation_list\":[],\"track_rating\":18,\"commontrack_id\":19436031,\"instrumental\":0,\"explicit\":1,\"has_lyrics\":1,\"has_subtitles\":0,\"has_richsync\":0,\"num_favourite\":0,\"album_id\":20931543,\"album_name\":\"Les Grands Moments Live (Live A L\\u2019Olympia 2013)\",\"artist_id\":25616,\"artist_name\":\"Michel Sardou\",\"track_share_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Michel-Sardou\\/Rouge-Live-%C3%A0-l-Olympia-2013?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"track_edit_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Michel-Sardou\\/Rouge-Live-%C3%A0-l-Olympia-2013\\/edit?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"restricted\":0,\"updated_time\":\"2014-01-23T10:46:12Z\",\"primary_genres\":{\"music_genre_list\":[{\"music_genre\":{\"music_genre_id\":50000064,\"music_genre_parent_id\":34,\"music_genre_name\":\"French Pop\",\"music_genre_name_extended\":\"French Pop\",\"music_genre_vanity\":\"French-Pop\"}}]}}},{\"track\":{\"track_id\":93587741,\"track_name\":\"Rouge - Live Bercy 2001\",\"track_name_translation_list\":[],\"track_rating\":1,\"commontrack_id\":16264023,\"instrumental\":0,\"explicit\":1,\"has_lyrics\":1,\"has_subtitles\":0,\"has_richsync\":0,\"num_favourite\":0,\"album_id\":21503707,\"album_name\":\"Bercy 2001\",\"artist_id\":25616,\"artist_name\":\"Michel Sardou\",\"track_share_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Michel-Sardou\\/Rouge-Live-Bercy-2001?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"track_edit_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Michel-Sardou\\/Rouge-Live-Bercy-2001\\/edit?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"restricted\":0,\"updated_time\":\"2013-12-29T21:21:14Z\",\"primary_genres\":{\"music_genre_list\":[{\"music_genre\":{\"music_genre_id\":50000064,\"music_genre_parent_id\":34,\"music_genre_name\":\"French Pop\",\"music_genre_name_extended\":\"French Pop\",\"music_genre_vanity\":\"French-Pop\"}}]}}},{\"track\":{\"track_id\":39651075,\"track_name\":\"Rouge (Live)\",\"track_name_translation_list\":[],\"track_rating\":1,\"commontrack_id\":16264446,\"instrumental\":0,\"explicit\":1,\"has_lyrics\":1,\"has_subtitles\":0,\"has_richsync\":0,\"num_favourite\":0,\"album_id\":15876810,\"album_name\":\"Confidences et retrouvailles (Live)\",\"artist_id\":25616,\"artist_name\":\"Michel Sardou\",\"track_share_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Michel-Sardou\\/Rouge-Live?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"track_edit_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Michel-Sardou\\/Rouge-Live\\/edit?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"restricted\":0,\"updated_time\":\"2013-12-29T21:30:11Z\",\"primary_genres\":{\"music_genre_list\":[{\"music_genre\":{\"music_genre_id\":50000064,\"music_genre_parent_id\":34,\"music_genre_name\":\"French Pop\",\"music_genre_name_extended\":\"French Pop\",\"music_genre_vanity\":\"French-Pop\"}}]}}},{\"track\":{\"track_id\":93587779,\"track_name\":\"Rouge - Live Bercy 98\",\"track_name_translation_list\":[],\"track_rating\":1,\"commontrack_id\":16263710,\"instrumental\":0,\"explicit\":1,\"has_lyrics\":1,\"has_subtitles\":1,\"has_richsync\":0,\"num_favourite\":0,\"album_id\":21503709,\"album_name\":\"Bercy 98\",\"artist_id\":25616,\"artist_name\":\"Michel Sardou\",\"track_share_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Michel-Sardou\\/Rouge-Live-Bercy-98?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"track_edit_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Michel-Sardou\\/Rouge-Live-Bercy-98\\/edit?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"restricted\":0,\"updated_time\":\"2013-12-29T21:10:24Z\",\"primary_genres\":{\"music_genre_list\":[{\"music_genre\":{\"music_genre_id\":14,\"music_genre_parent_id\":34,\"music_genre_name\":\"Pop\",\"music_genre_name_extended\":\"Pop\",\"music_genre_vanity\":\"Pop\"}},{\"music_genre\":{\"music_genre_id\":50000064,\"music_genre_parent_id\":34,\"music_genre_name\":\"French Pop\",\"music_genre_name_extended\":\"French Pop\",\"music_genre_vanity\":\"French-Pop\"}}]}}},{\"track\":{\"track_id\":2026597,\"track_name\":\"Intro : Du noir au rouge \\/ Le Successeur\",\"track_name_translation_list\":[],\"track_rating\":1,\"commontrack_id\":2177806,\"instrumental\":0,\"explicit\":0,\"has_lyrics\":0,\"has_subtitles\":0,\"has_richsync\":0,\"num_favourite\":0,\"album_id\":10384751,\"album_name\":\"Olympia 95\",\"artist_id\":25616,\"artist_name\":\"Michel Sardou\",\"track_share_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Michel-Sardou\\/Intro-Du-noir-au-rouge-Le-Successeur?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"track_edit_url\":\"https:\\/\\/www.musixmatch.com\\/lyrics\\/Michel-Sardou\\/Intro-Du-noir-au-rouge-Le-Successeur\\/edit?utm_source=application&utm_campaign=api&utm_medium=FribourgSDP%3A1409622437882\",\"restricted\":0,\"updated_time\":\"2011-06-12T10:21:00Z\",\"primary_genres\":{\"music_genre_list\":[]}}}]}}}"
                }else if(req.toString() == "Request{method=GET, url=http://api.musixmatch.com/ws/1.1/track.lyrics.get?track_id=39648585&apikey=a3454edb65483e706c127deaa11df69d, tag=null}"){
                    "{\"message\":{\"header\":{\"status_code\":200,\"execute_time\":0.0066251754760742},\"body\":{\"lyrics\":{\"lyrics_id\":18905350,\"explicit\":1,\"lyrics_body\":\"Rouge\\nComme un soleil couchant de M\\u00e9diterran\\u00e9e\\nRouge\\nComme le vin de Bordeaux dans ma t\\u00eate \\u00e9toil\\u00e9e\\nRouge\\nComme le sang de Rimbaud coulant sur un cahier\\nRouge\\nComme la mer qui recouvre le d\\u00e9sert de Jud\\u00e9e\\n\\nRouge\\nComme les joues d'un enfant quand il a trop jou\\u00e9\\nRouge\\nComme la pomme qui te donne le parfum du p\\u00each\\u00e9\\nRouge\\nComme le feu du volcan qui va se r\\u00e9veiller\\nRouge\\nComme cette \\u00e9toile au c\\u0153ur de ce dormeur couch\\u00e9\\n\\nComme un oiseau tu\\u00e9 par un chasseur tragique\\nComme un acteur bless\\u00e9 par les cris du public\\nComme un violon bris\\u00e9 qui rejoue l'H\\u00e9ro\\u00efque\\n...\\n\\n******* This Lyrics is NOT for Commercial use *******\\n(1409622437882)\",\"script_tracking_url\":\"https:\\/\\/tracking.musixmatch.com\\/t1.0\\/m_js\\/e_1\\/sn_0\\/l_18905350\\/su_0\\/rs_0\\/tr_3vUCAFBoqXZ6JoIfCALGM-kVkQJp5lB9wJ8DXFZpZbAo4fskV8rLrRsH5pFGuPYeEHoNQgikxgiqZCWR2lCh-EPeoesCuMp6pDWbQ0ZnH7KK_RZoHea9KUpefqu2m9EODo7hUXquRaDmmQ7XD2IS8-Ni01yF16dyep8SnzojiEQxjvMgdwi73Ognye7Er7sMt6-cuHcrGpNp31JITMJnA-GXdXSNb2RYcR2zIAuoz7-cLmlQ4NtsT-y8AK81fZHFZN6SEWA3kZnVyThuc3iuLAhp9BY-DxNA4llF1O5vRrrN4SxgdD5Yc4P40AbWHghvmMPo8wJlja-fwaxOrj32vsuEecFWZJW-9pgixOUbgU6Trhj_xCMR9--cmQ9w0DRyz1pKXOxpfT1x4P3yNLLr5yEdrCccMVEYWmIvIEWUWTan6ca832wMLgOIcBPCuJo\\/\",\"pixel_tracking_url\":\"https:\\/\\/tracking.musixmatch.com\\/t1.0\\/m_img\\/e_1\\/sn_0\\/l_18905350\\/su_0\\/rs_0\\/tr_3vUCAFQiwL2GaLY-KjrEu94O9hJ6qrsr0SPyu6-7MZ32ixvieueABqrXdbGHGK6Ba7kudQyGkufkTgBFZ9nUPUrDHF9WklCdNYbD2CwouqEItv4Rb8cqu4xm4s8z0ce_zccrrbFMR4TncPzCw0uij5jB4_sTQH6l87Xk5dJYSYus5maOaXaKfXsxgOmhM-MofOGgAi9sR9DJ7KOOWnVQne2o6ZVq4vR6esy6-G2a__LvddQ471DiCawtijhWDsktnZxRllby3hKmy_1yVpYau6RI3kiD6ZRro9j3oAgKY8eSVIDf014yygpHL3RAUYamzMyDeFYASNPjihid7YZNQmpCE9sCOpNlzHy9qji066ZoWI-u0FdsvIg_kkqpSeo8JNpUAUdI55loscd3s8_mTUzgV70lB2qUhS4kgyOq25w6mnzkpM7B0Y4obUr5Zs8\\/\",\"lyrics_copyright\":\"Lyrics powered by www.musixmatch.com. This Lyrics is NOT for Commercial use and only 30% of the lyrics are returned.\",\"updated_time\":\"2022-03-10T13:57:35Z\"}}}}"
                }else{
                    "ERROR"
                }
                rc.onResponse(this, Response.Builder().message("OK").request(req).protocol(Protocol.HTTP_1_0).code(200).body(
                    ResponseBody.create(
                        MediaType.parse(s),s)).build())
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
        class SemiFailingCall(private val request : Request) : Call{

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
                    MockOkHttpClient().newCall(request).enqueue(responseCallback)
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
    class NullJSONParser() : JSONParser() {
        override fun parse(s: String?): JSONObject? {
            return null
        }
    }
    class EmptyLyricsJSONParser : JSONParser(){
        override fun parse(s: String?): JSONObject {
            return JSONObject("{\"message\":{\"header\":{\"status_code\":200,\"execute_time\":0.0089538097381592},\"body\":{\"lyrics\":{\"lyrics_id\":18905350,\"explicit\":1,\"lyrics_body\":\"\"}}}}")
        }
    }

}
