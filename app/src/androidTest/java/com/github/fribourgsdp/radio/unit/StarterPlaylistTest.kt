package com.github.fribourgsdp.radio.unit

import androidx.test.platform.app.InstrumentationRegistry
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.data.StarterPlaylists
import org.junit.Assert
import org.junit.Test

class StarterPlaylistTest {
    @Test
    fun allStarterPlaylistsAreLoaded(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val userPlaylists = StarterPlaylists.getStarterPlaylists(context)
        Assert.assertTrue(userPlaylists.contains(Playlist("Chansons francophones")))
        Assert.assertTrue(userPlaylists.contains(Playlist("Basic blind test")))
        Assert.assertTrue(userPlaylists.contains(Playlist("Movie theme songs")))
        Assert.assertTrue(userPlaylists.contains(Playlist("Video game songs")))
        Assert.assertTrue(userPlaylists.contains(Playlist("Classical music hits")))
    }

    @Test
    fun chansonsFrancophonesPlaylistHasGoodTitlesAndArtists(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val chansonsFrancophonesPlaylist = StarterPlaylists.getStarterPlaylists(context).find { p -> p.name == "Chansons francophones" }!!
        Assert.assertFalse(
            chansonsFrancophonesPlaylist.getSongs().contains(Song("Jacques Brel", "Les vieux"))
        )
        Assert.assertTrue(
            chansonsFrancophonesPlaylist.getSongs()
                .contains(Song("Foule sentimentale", "Alain Souchon"))
        )
    }
}
