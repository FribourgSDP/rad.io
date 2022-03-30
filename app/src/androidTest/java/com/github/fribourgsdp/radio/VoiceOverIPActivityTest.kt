package com.github.fribourgsdp.radio

import androidx.test.espresso.Espresso
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
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
import org.junit.Rule
import org.junit.Test
import java.util.ArrayList

class VoiceOverIPActivityTest {
    @get:Rule
    var voiceOverIpActivityRule = ActivityScenarioRule(VoiceOverIPActivity::class.java)


    @Test
    fun pressBackWorks(){
        Intents.init()
        Espresso.pressBack()
        Intents.intended(
            IntentMatchers.hasComponent(MainActivity::class.java.name)
        )
        Intents.release()
    }


}
