package com.github.fribourgsdp.radio

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageButton
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

    @Test
    fun initMockRtcEngine() {
        Intents.init()
        val context: Context = ApplicationProvider.getApplicationContext()



        val intent: Intent = Intent(context, VoiceOverIPActivity::class.java)

        ActivityScenario.launch<VoiceOverIPActivity>(intent).use { scenario ->
            scenario.onActivity { activity ->
                val testEngineDecorator : VoiceIpEngineDecorator = VoiceIpEngineDecorator(activity, true)
            }

            Espresso.pressBack()
            Intents.intended(
                Matchers.allOf(
                    IntentMatchers.hasComponent(MainActivity::class.java.name),
                    IntentMatchers.toPackage("com.github.fribourgsdp.radio")
                )
            )
        }
        Intents.release()
    }

    @Test
    fun allFunctionsWeNeedToCallReturnSuccess(){
        val context: Context = ApplicationProvider.getApplicationContext()

        val intent: Intent = Intent(context, VoiceOverIPActivity::class.java)

        ActivityScenario.launch<VoiceOverIPActivity>(intent).use { scenario ->
            scenario.onActivity { activity ->
                val testEngineDecorator = VoiceIpEngineDecorator(activity, true)
                assertTrue(testEngineDecorator.joinChannel(null, null, null, 3) == 0)
                assertTrue(testEngineDecorator.joinChannel(null, null, null, 3, null) == 0)
                assertTrue(testEngineDecorator.enableAudioVolumeIndication(5, 3, true) == 0)
                assertTrue(testEngineDecorator.setAudioProfile(3, 5) == 0)
                assertTrue(testEngineDecorator.leaveChannel() == 0)
                assertTrue(testEngineDecorator.muteLocalAudioStream(true) == 0)
            }
        }


        val testEngineDecorator : VoiceIpEngineDecorator = VoiceIpEngineDecorator(context, true)
        assertTrue(testEngineDecorator.joinChannel(null, null, null, 3) == 0)
        assertTrue(testEngineDecorator.enableAudioVolumeIndication(5, 3, true) == 0)
        assertTrue(testEngineDecorator.setAudioProfile(3, 5) == 0)
        assertTrue(testEngineDecorator.leaveChannel() == 0)
        assertTrue(testEngineDecorator.muteLocalAudioStream(true) == 0)
        val testImageButton = ImageButton(context)
        testEngineDecorator.mute(testImageButton)
        testEngineDecorator.mute(testImageButton)
    }

}