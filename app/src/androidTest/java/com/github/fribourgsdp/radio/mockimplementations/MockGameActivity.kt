package com.github.fribourgsdp.radio.mockimplementations

import android.content.Context
import android.os.Bundle
import com.github.fribourgsdp.radio.data.Genre
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.external.makeMockIRtcEngineEventHandler
import com.github.fribourgsdp.radio.external.makeMockRtcEngine
import com.github.fribourgsdp.radio.game.GameActivity
import com.github.fribourgsdp.radio.utils.playListName
import com.github.fribourgsdp.radio.utils.songName
import com.github.fribourgsdp.radio.utils.userName
import com.github.fribourgsdp.radio.voip.VoiceIpEngineDecorator
import org.mockito.Mockito

class MockGameActivity : GameActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        User.setFSGetter(MockFileSystem.MockFSGetter)
        val user = User(userName, 0)
        val playlist1 = Playlist(playListName, Genre.ROCK)
        val song = Song(songName, "artist")
        playlist1.addSong(song)
        user.addPlaylists(setOf(playlist1))
        user.save(Mockito.mock(Context::class.java))
        super.onCreate(savedInstanceState)
    }

    override fun initVoiceChat(gameUid: Long) {
        voiceChannel = VoiceIpEngineDecorator(this, makeMockIRtcEngineEventHandler(), makeMockRtcEngine())
        super.initVoiceChat(gameUid)
    }
}

