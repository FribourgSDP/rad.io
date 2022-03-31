package com.github.fribourgsdp.radio

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.rule.GrantPermissionRule
import io.agora.rtc.*
import io.agora.rtc.audio.AudioRecordingConfiguration
import io.agora.rtc.gl.EglBase
import io.agora.rtc.internal.EncryptionConfig
import io.agora.rtc.internal.LastmileProbeConfig
import io.agora.rtc.live.LiveInjectStreamConfig
import io.agora.rtc.live.LiveTranscoding
import io.agora.rtc.mediaio.IVideoSink
import io.agora.rtc.mediaio.IVideoSource
import io.agora.rtc.models.ChannelMediaOptions
import io.agora.rtc.models.ClientRoleOptions
import io.agora.rtc.models.DataStreamConfig
import io.agora.rtc.models.UserInfo
import io.agora.rtc.video.*
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test
import java.util.ArrayList

class VoiceOverIPActivityTest {

    @get:Rule var permissionRule = GrantPermissionRule.grant(android.Manifest.permission.RECORD_AUDIO)

    @Test
    fun pressBackWorks(){
        Intents.init()
        val context: Context = ApplicationProvider.getApplicationContext()

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
        Intents.release()
    }


}
