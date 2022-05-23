package com.github.fribourgsdp.radio.voip


import android.annotation.SuppressLint
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.fribourgsdp.radio.R
import io.agora.rtc.IRtcEngineEventHandler
class MyIRtcEngineEventHandler (private val appCompatActivity: AppCompatActivity, private val mapHashToName : Map<Int,String>) :IRtcEngineEventHandler() {
    @SuppressLint("SetTextI18n")
    override fun onActiveSpeaker(uid: Int) {
        super.onActiveSpeaker(uid)
        val activeSpeakerView : TextView = appCompatActivity.findViewById(R.id.activeSpeakerView)
        activeSpeakerView.invalidate()
        activeSpeakerView.text = mapHashToName[uid]?.let {
            appCompatActivity.getString(R.string.active_speaker_format, it)
        } ?: ""
    }
}
