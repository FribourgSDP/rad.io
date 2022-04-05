package com.github.fribourgsdp.radio

import android.content.Context
import android.util.Log
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.models.ChannelMediaOptions
import java.lang.Exception


const val APP_ID = "971046257e964b73bafc7f9458fa9996"
const val CERTIFICATE = "5822360c68714dcca1382167d4070673"
const val EXPIRATION_TIME = 10800

class VoiceIpEngineDecorator (context: Context){
    private var voiceChatEngine: RtcEngine
    private val mRtcEventHandler = object : IRtcEngineEventHandler() {}

    init {
        var mRtcEngine: RtcEngine? = null
        while (mRtcEngine == null){
            try {
                mRtcEngine = RtcEngine.create(context, APP_ID, mRtcEventHandler)
            } catch (e: Exception) {
                Log.e("Error while loading the voice channel.", e.toString())
            }
        }
        voiceChatEngine = mRtcEngine
    }

    constructor(context: Context, engine: RtcEngine) : this(context) {
        voiceChatEngine = engine
    }

    fun joinChannel(
        token: String?,
        channelName: String?,
        optionalInfo: String?,
        optionalUid: Int
    ): Int {
        return voiceChatEngine.joinChannel(token, channelName, optionalInfo, optionalUid)
    }

    fun joinChannel(
        token: String?,
        channelName: String?,
        optionalInfo: String?,
        optionalUid: Int,
        options: ChannelMediaOptions?
    ): Int {
        return voiceChatEngine.joinChannel(token, channelName, optionalInfo, optionalUid, options)
    }

    fun setAudioProfile(profile: Int, scenario: Int): Int {
        return voiceChatEngine.setAudioProfile(profile, scenario)
    }

    fun enableAudioVolumeIndication(interval: Int, smooth: Int, report_vad: Boolean): Int {
        return voiceChatEngine.enableAudioVolumeIndication(interval, smooth, report_vad)
    }

}