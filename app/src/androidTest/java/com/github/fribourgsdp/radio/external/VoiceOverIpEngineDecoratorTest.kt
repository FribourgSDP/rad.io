package com.github.fribourgsdp.radio.external

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import com.github.fribourgsdp.radio.MainActivity
import com.github.fribourgsdp.radio.utils.packageName
import com.github.fribourgsdp.radio.deprecated.VoiceOverIPActivity
import com.github.fribourgsdp.radio.voip.VoiceIpEngineDecorator
import io.agora.rtc.IRtcEngineEventHandler
import io.agora.rtc.RtcEngine
import org.junit.Assert.assertTrue
import org.hamcrest.Matchers
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*


class VoiceOverIpEngineDecoratorTest {

    @Before
    fun initIntent() {
        Intents.init()
    }

    @After
    fun releaseIntent() {
        Intents.release()
    }

    @Test
    fun initMockRtcEngine() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, VoiceOverIPActivity::class.java)

        ActivityScenario.launch<VoiceOverIPActivity>(intent).use {

            VoiceIpEngineDecorator(context, makeMockIRtcEngineEventHandler(), makeMockRtcEngine())

            Espresso.pressBack()
            Intents.intended(
                Matchers.allOf(
                    IntentMatchers.hasComponent(MainActivity::class.java.name),
                    IntentMatchers.toPackage(packageName)
                )
            )
        }
    }

    @Test
    fun allFunctionsWeNeedToCallReturnSuccess() {
        val context: Context = ApplicationProvider.getApplicationContext()

        val intent = Intent(context, VoiceOverIPActivity::class.java)

        ActivityScenario.launch<VoiceOverIPActivity>(intent).use {
            val testEngineDecorator =
                VoiceIpEngineDecorator(context, makeMockIRtcEngineEventHandler(), makeMockRtcEngine())
            assertTrue(testEngineDecorator.joinChannel(null, null, null, 3) == 0)
            assertTrue(testEngineDecorator.enableAudioVolumeIndication(5, 3, true) == 0)
            assertTrue(testEngineDecorator.setAudioProfile(3, 5) == 0)
            assertTrue(testEngineDecorator.leaveChannel() == 0)
            assertTrue(testEngineDecorator.muteLocalAudioStream(true) == 0)
        }
    }

}

fun makeMockRtcEngine() : RtcEngine {
    val mockEngine = mock(RtcEngine::class.java)
    `when`(mockEngine.joinChannel(any(), any(), any(), anyInt())).thenReturn(0)
    `when`(mockEngine.enableAudioVolumeIndication(anyInt(), anyInt(), anyBoolean())).thenReturn(0)
    `when`(mockEngine.setAudioProfile(anyInt(), anyInt())).thenReturn(0)
    `when`(mockEngine.leaveChannel()).thenReturn(0)
    `when`(mockEngine.muteLocalAudioStream(anyBoolean())).thenReturn(0)
    `when`(mockEngine.setDefaultAudioRoutetoSpeakerphone(anyBoolean())).thenReturn(0)
    return mockEngine
}

fun makeMockIRtcEngineEventHandler() : IRtcEngineEventHandler {
    val iRtcEngineEventHandler = mock(IRtcEngineEventHandler::class.java)
    return iRtcEngineEventHandler
}