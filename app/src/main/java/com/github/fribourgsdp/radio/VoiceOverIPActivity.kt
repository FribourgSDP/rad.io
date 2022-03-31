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
import RtcTokenBuilder.RtcTokenBuilder
import android.util.Log

const val MAX_SERVER_CONNECT_TENTATIVES = 5

class VoiceOverIPActivity : AppCompatActivity() {

    private val APP_ID = "971046257e964b73bafc7f9458fa9996"
    private val CHANNEL = "test44"
    private val CERTIFICATE = "5822360c68714dcca1382167d4070673"
    private val TOKEN = "006971046257e964b73bafc7f9458fa9996IABHFc2VemmVSekHkloRBGwdRXrfr49nBFNBC2+4szfUboESWpqbjtJtIgB5s8RbnuNFYgQAAQBNvERiAgBNvERiAwBNvERiBABNvERi"
    private val EXPIRATION_TIME = 10800

    private var mRtcEngine: RtcEngine ?= null
    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
    }

    private val uid = 1
    private lateinit var token : String
    private val PERMISSION_REQ_ID_RECORD_AUDIO = 22
    private val PERMISSION_REQ_ID_CAMERA = PERMISSION_REQ_ID_RECORD_AUDIO + 1
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
        setContentView(R.layout.activity_voice_over_ip)
        token = getToken(uid)
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO, PERMISSION_REQ_ID_RECORD_AUDIO)) {
            initializeAndJoinChannel();
        }
    }

    private fun initializeAndJoinChannel() {
        var nbTentatives = 0
        while (mRtcEngine == null && nbTentatives < MAX_SERVER_CONNECT_TENTATIVES){
            try {
                mRtcEngine = RtcEngine.create(baseContext, APP_ID, mRtcEventHandler)
            } catch (e: Exception) {
                nbTentatives += 1
                Log.e("Error while loading the voice channel.", e.toString())
            }
        }
        if (nbTentatives != MAX_SERVER_CONNECT_TENTATIVES){
            mRtcEngine!!.joinChannel(token, CHANNEL, "", uid)
        }

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


    private fun getToken(uid:Int) : String{
        val token = RtcTokenBuilder()
        val timestamp = (System.currentTimeMillis() / 1000 + EXPIRATION_TIME).toInt()

        val result = token.buildTokenWithUid(
            APP_ID, CERTIFICATE,
            CHANNEL, uid, RtcTokenBuilder.Role.Role_Publisher, timestamp
        )
        return result
    }
}