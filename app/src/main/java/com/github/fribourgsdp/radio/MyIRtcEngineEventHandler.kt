package com.github.fribourgsdp.radio


import android.annotation.SuppressLint
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.agora.rtc.IRtcEngineEventHandler
class MyIRtcEngineEventHandler (private val appCompatActivity: AppCompatActivity) :IRtcEngineEventHandler() {
    @SuppressLint("SetTextI18n")
    override fun onActiveSpeaker(uid: Int) {
        super.onActiveSpeaker(uid)
        val activeSpeakerView : TextView = appCompatActivity.findViewById(R.id.activeSpeaker)
        activeSpeakerView.invalidate()
        activeSpeakerView.setText("active speaker : $uid")
    }
}
