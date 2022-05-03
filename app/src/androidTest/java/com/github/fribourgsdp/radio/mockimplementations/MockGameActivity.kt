package com.github.fribourgsdp.radio.mockimplementations

import com.github.fribourgsdp.radio.*
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import org.mockito.Mockito


class MockGameActivity : GameActivity() {
    override fun initVoiceChat(gameUid: Long) {
        voiceChannel = VoiceIpEngineDecorator(this, makeMockIRtcEngineEventHandler(), makeMockRtcEngine())
        super.initVoiceChat(gameUid)
    }
}

