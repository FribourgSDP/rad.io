package com.github.fribourgsdp.radio

import RtcTokenBuilder.RtcTokenBuilder
import android.content.Context
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import io.agora.rtc.models.ChannelMediaOptions
import java.lang.Exception

const val APP_ID = "971046257e964b73bafc7f9458fa9996"
const val CERTIFICATE = "5822360c68714dcca1382167d4070673"
const val EXPIRATION_TIME = 10800

class VoiceIpEngineDecorator : java.io.Serializable {
    private var voiceChatEngine: RtcEngine
    private val mRtcEventHandler : IRtcEngineEventHandler
    private var isTesting: Boolean = false
    private var isMuted: Boolean = false
    private val context : Context




    constructor(appCompatActivity: AppCompatActivity){
        context = appCompatActivity.applicationContext
        mRtcEventHandler = MyIRtcEngineEventHandler(appCompatActivity)
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


    constructor(appCompatActivity: AppCompatActivity, rtcEngine: RtcEngine, mRtcEventHandler : IRtcEngineEventHandler)  {
        context = appCompatActivity.applicationContext
        this.mRtcEventHandler = mRtcEventHandler
        voiceChatEngine = rtcEngine
    }

    fun joinChannel(
        token: String?,
        channelName: String?,
        optionalInfo: String?,
        optionalUid: Int
    ): Int {
        return voiceChatEngine.joinChannel(token, channelName, optionalInfo, optionalUid)
    }


    fun setAudioProfile(profile: Int, scenario: Int): Int {
        return voiceChatEngine.setAudioProfile(profile, scenario)
    }

    fun enableAudioVolumeIndication(interval: Int, smooth: Int, report_vad: Boolean): Int {
        return voiceChatEngine.enableAudioVolumeIndication(interval, smooth, report_vad)
    }

    fun leaveChannel(): Int {
        return voiceChatEngine.leaveChannel()
    }

    fun muteLocalAudioStream(is_muted: Boolean): Int {
        return voiceChatEngine.muteLocalAudioStream(is_muted)
    }

    fun getToken(uid:Int, channel: String) : String{
        val token = RtcTokenBuilder()
        val timestamp = (System.currentTimeMillis() / 1000 + EXPIRATION_TIME).toInt()

        val result = token.buildTokenWithUid(
            APP_ID, CERTIFICATE,
            channel, uid, RtcTokenBuilder.Role.Role_Publisher, timestamp
        )
        return result
    }

    fun mute(muteButton: ImageButton) {
        val ret = voiceChatEngine.muteLocalAudioStream(isMuted xor true)
        if(ret == 0) {
            isMuted = isMuted xor true
            if(isMuted){
                muteButton.setImageResource(R.drawable.ic_mute)
            }else{
                muteButton.setImageResource(R.drawable.ic_unmute)
            }
        }
    }
}