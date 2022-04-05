package com.github.fribourgsdp.radio

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import com.github.fribourgsdp.radio.mockimplementations.MockRtcEngine
import junit.framework.Assert.assertTrue
import org.hamcrest.Matchers
import org.junit.Test

class VoiceOverIpEngineDecoratorTest {
    @Test
    fun initMockRtcEngine() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val mockEngine = MockRtcEngine()
        val testEngineDecorator : VoiceIpEngineDecorator = VoiceIpEngineDecorator(context, mockEngine)
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
        val mockEngine = MockRtcEngine()
        val testEngineDecorator : VoiceIpEngineDecorator = VoiceIpEngineDecorator(context, mockEngine)
        assertTrue(testEngineDecorator.joinChannel(null, null, null, 3) == 0)
        assertTrue(testEngineDecorator.joinChannel(null, null, null, 3, null) == 0)
        assertTrue(testEngineDecorator.enableAudioVolumeIndication(5, 3, true) == 0)
        assertTrue(testEngineDecorator.setAudioProfile(3, 5) == 0)
    }

}