package com.github.fribourgsdp.radio


import android.annotation.SuppressLint
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import io.agora.rtc.IRtcEngineEventHandler
class MyIRtcEngineEventHandler (private val appCompatActivity: AppCompatActivity, private val mapHashToName : Map<Int,String>) :IRtcEngineEventHandler() {
    @SuppressLint("SetTextI18n")
    override fun onActiveSpeaker(uid: Int) {
        super.onActiveSpeaker(uid)
        val activeSpeakerView : TextView = appCompatActivity.findViewById(R.id.activeSpeakerView)
        activeSpeakerView.invalidate()
        val name = mapHashToName.getOrDefault(uid, "Null")
        activeSpeakerView.setText("active speaker : $name")
    }
}
