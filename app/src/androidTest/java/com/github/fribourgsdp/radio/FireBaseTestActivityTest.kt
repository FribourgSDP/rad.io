package com.github.fribourgsdp.radio

import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.github.fribourgsdp.radio.activities.testActivities.FireBaseTestActivity
import org.junit.Rule
import org.junit.Test

class FireBaseTestActivityTest {

    @get:Rule
    var FireBaseTestRule = ActivityScenarioRule(FireBaseTestActivity::class.java)

    //this class is to test the Database feature easily, it doesn't have any functionality so I just run the activity to have the coverage
    @Test
    fun launchTheActivity(){
        Intents.init()
        Intents.release()
    }


}