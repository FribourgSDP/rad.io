package com.github.fribourgsdp.radio

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import io.agora.rtc.RtcEngine
import junit.framework.Assert.assertTrue
import org.hamcrest.Matchers
import org.junit.Test
import org.mockito.Mockito.*


class VoiceOverIpEngineDecoratorTest {

    fun makeMockRtcEngine() : RtcEngine {
        val mockEngine = mock(RtcEngine::class.java)
        `when`(mockEngine.joinChannel(any(), any(), any(), any())).thenReturn(0)
        `when`(mockEngine.joinChannel(any(), any(), any(), any(), any())).thenReturn(0)
        `when`(mockEngine.enableAudioVolumeIndication(any(), any(), any())).thenReturn(0)
        `when`(mockEngine.setAudioProfile(any(), any())).thenReturn(0)
        return mockEngine
    }

    @Test
    fun initMockRtcEngine() {
        val context: Context = ApplicationProvider.getApplicationContext()

        val testEngineDecorator : VoiceIpEngineDecorator = VoiceIpEngineDecorator(context, makeMockRtcEngine())
        val intent: Intent = Intent(context, VoiceOverIPActivity::class.java)
        ActivityScenario.launch<VoiceOverIPActivity>(intent).use { scenario ->
            Espresso.pressBack()
            Intents.intended(
                Matchers.allOf(
                    IntentMatchers.hasComponent(MainActivity::class.java.name),
                    IntentMatchers.toPackage("com.github.fribourgsdp.radio")
                )
            )
        }
    }

    @Test
    fun allFunctionsWeNeedToCallReturnSuccess(){
        val context: Context = ApplicationProvider.getApplicationContext()
        val testEngineDecorator : VoiceIpEngineDecorator = VoiceIpEngineDecorator(context, makeMockRtcEngine())
        assertTrue(testEngineDecorator.joinChannel(null, null, null, 3) == 0)
        assertTrue(testEngineDecorator.joinChannel(null, null, null, 3, null) == 0)
        assertTrue(testEngineDecorator.enableAudioVolumeIndication(5, 3, true) == 0)
        assertTrue(testEngineDecorator.setAudioProfile(3, 5) == 0)
    }

}