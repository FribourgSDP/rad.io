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


fun makeMockRtcEngine() : RtcEngine {
    val mockEngine = Mockito.mock(RtcEngine::class.java)
    Mockito.`when`(
        mockEngine.joinChannel(
            Mockito.any(),
            Mockito.any(),
            Mockito.any(),
            Mockito.anyInt()
        )
    ).thenReturn(0)
    Mockito.`when`(
        mockEngine.enableAudioVolumeIndication(
            Mockito.anyInt(),
            Mockito.anyInt(),
            Mockito.anyBoolean()
        )
    ).thenReturn(0)
    Mockito.`when`(mockEngine.setAudioProfile(Mockito.anyInt(), Mockito.anyInt())).thenReturn(0)
    Mockito.`when`(mockEngine.leaveChannel()).thenReturn(0)
    Mockito.`when`(mockEngine.muteLocalAudioStream(Mockito.anyBoolean())).thenReturn(0)
    return mockEngine
}

fun makeMockIRtcEngineEventHandler() : IRtcEngineEventHandler {
    val iRtcEngineEventHandler = Mockito.mock(IRtcEngineEventHandler::class.java)
    return iRtcEngineEventHandler
}