package com.github.fribourgsdp.radio

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.rule.GrantPermissionRule
import org.hamcrest.Matchers
import org.junit.Rule
import org.junit.Test

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
