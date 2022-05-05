package com.github.fribourgsdp.radio.mockimplementations

import com.github.fribourgsdp.radio.*
import com.github.fribourgsdp.radio.game.GameActivity
import com.github.fribourgsdp.radio.voip.VoiceIpEngineDecorator

class MockGameActivity : GameActivity() {
    override fun initVoiceChat(gameUid: Long) {
        voiceChannel = VoiceIpEngineDecorator(this, makeMockIRtcEngineEventHandler(), makeMockRtcEngine())
        super.initVoiceChat(gameUid)
    }
}

