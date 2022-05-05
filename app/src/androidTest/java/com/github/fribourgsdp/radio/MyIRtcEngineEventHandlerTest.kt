package com.github.fribourgsdp.radio

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.ViewMatchers
import com.github.fribourgsdp.radio.mockimplementations.MockGameActivity
import com.github.fribourgsdp.radio.voip.MyIRtcEngineEventHandler
import org.junit.After
import org.junit.Before
import org.junit.Test

class MyIRtcEngineEventHandlerTest {


    @Before
    fun initIntent() {
        Intents.init()
    }

    @After
    fun releaseIntent() {
        Intents.release()
    }
    @Test
    fun existingUserSpeak(){
        val context: Context = ApplicationProvider.getApplicationContext()


        val activeSpeakerView = Espresso.onView(ViewMatchers.withId(R.id.activeSpeakerView))

        val intent = Intent(context, MockGameActivity::class.java)
        ActivityScenario.launch<MockGameActivity>(intent).use { scenario ->
            scenario.onActivity { activity ->


                val map = hashMapOf(Pair(1,"Pierre"))
                val myIRtcEngineEventHandler = MyIRtcEngineEventHandler(activity,map)
                myIRtcEngineEventHandler.onActiveSpeaker(1)

            }

            activeSpeakerView.check(
                ViewAssertions.matches(
                    ViewMatchers.withText("active speaker : Pierre")
                )
            )

        }
    }



    @Test
    fun nonExistingUserSpeak(){
        val context: Context = ApplicationProvider.getApplicationContext()


        val activeSpeakerView = Espresso.onView(ViewMatchers.withId(R.id.activeSpeakerView))

        val intent = Intent(context, MockGameActivity::class.java)
        ActivityScenario.launch<MockGameActivity>(intent).use { scenario ->
            scenario.onActivity { activity ->


                val map = hashMapOf(Pair(1, "Pierre"))
                val myIRtcEngineEventHandler = MyIRtcEngineEventHandler(activity, map)
                myIRtcEngineEventHandler.onActiveSpeaker(2)
            }

            activeSpeakerView.check(
                ViewAssertions.matches(
                    ViewMatchers.withText("active speaker : Null")
                )
            )
        }


    }


}
