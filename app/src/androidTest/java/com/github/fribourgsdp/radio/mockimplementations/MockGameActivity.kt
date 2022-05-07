package com.github.fribourgsdp.radio.mockimplementations

import android.content.Context
import com.github.fribourgsdp.radio.data.Genre
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.external.makeMockIRtcEngineEventHandler
import com.github.fribourgsdp.radio.external.makeMockRtcEngine
import com.github.fribourgsdp.radio.game.GameActivity
import com.github.fribourgsdp.radio.voip.VoiceIpEngineDecorator
import org.mockito.Mockito

class MockGameActivity : GameActivity() {
    override fun initVoiceChat(gameUid: Long) {
        User.setFSGetter(MockFileSystem.MockFSGetter)
        val user = User(userName, 0)
        val playlist1 = Playlist(playListName, Genre.ROCK)
        val song = Song(songName, "artist")
        playlist1.addSong(song)
        user.addPlaylists(setOf(playlist1))
        user.save(Mockito.mock(Context::class.java))
        voiceChannel = VoiceIpEngineDecorator(this, makeMockIRtcEngineEventHandler(), makeMockRtcEngine())
        super.initVoiceChat(gameUid)
    }
}

