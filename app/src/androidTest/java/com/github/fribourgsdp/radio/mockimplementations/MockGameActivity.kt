package com.github.fribourgsdp.radio.mockimplementations

import com.github.fribourgsdp.radio.*

class MockGameActivity : GameActivity() {
    override fun initVoiceChat(gameUid: Long) {
        voiceChannel = VoiceIpEngineDecorator(this, makeMockIRtcEngineEventHandler(), makeMockRtcEngine())
        super.initVoiceChat(gameUid)
    }
}

