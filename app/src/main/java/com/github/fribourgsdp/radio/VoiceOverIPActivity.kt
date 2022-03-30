package com.github.fribourgsdp.radio


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.Intent
import java.lang.Exception
import io.agora.rtc.RtcEngine
import io.agora.rtc.IRtcEngineEventHandler
import java.io.File
import java.io.FileInputStream
import java.util.*

const val AGORA_INVALID_USER = "This user should not be using the service."

class VoiceOverIPActivity : AppCompatActivity() {
    private val APP_ID: String = "971046257e964b73bafc7f9458fa9996"

    private val CHANNEL = "testNathanMaxence"
    private val CERTIFICATE = "5822360c68714dcca1382167d4070673"
    private val TOKEN = "006971046257e964b73bafc7f9458fa9996IAAbJr0QnsdkBXv2l5DoNI6N/JcK5z0gdkxdY24umGNotRwU6J0AAAAAEAAg4mLWLjNDYgEAAQAuM0Ni"
    private var mRtcEngine: RtcEngine ?= null
    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
    }

    private val PERMISSION_REQ_ID_RECORD_AUDIO = 22
    /*
        Check if the permissions necessary for voice calls are granted in the app.
        If they're nt granted, use Android built-in functionality to request them.

        Returns:
            -true if permissions are granted.
            -false if permissions are not yet granted.
     */
    private fun checkSelfPermission(permission: String, requestCode: Int): Boolean {
        if (ContextCompat.checkSelfPermission(this, permission) !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(permission),
                requestCode)
            return false
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_voice_over_ipactivity)
        println("APP id is $APP_ID")
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
            initializeAndJoinChannel();
        }
    }

    private fun initializeAndJoinChannel() {
        try {
            mRtcEngine = RtcEngine.create(baseContext, APP_ID, mRtcEventHandler)
        } catch (e: Exception) {
        }
        mRtcEngine!!.joinChannel(TOKEN, CHANNEL, "", 0)
    }


    override fun onDestroy() {
        super.onDestroy()
        mRtcEngine?.leaveChannel()
        RtcEngine.destroy()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}