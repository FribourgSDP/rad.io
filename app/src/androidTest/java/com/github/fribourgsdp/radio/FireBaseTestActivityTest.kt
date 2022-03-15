package com.github.fribourgsdp.radio

import androidx.test.espresso.intent.Intents
import androidx.test.ext.junit.rules.ActivityScenarioRule
import org.junit.Rule
import org.junit.Test

class FireBaseTestActivityTest {

    @get:Rule
    var FireBaseTestRule = ActivityScenarioRule(FireBaseTestActivity::class.java)

    //my
    @Test
    fun oui(){
        Intents.init()
    }


}