package com.github.fribourgsdp.radio

import org.junit.Assert.*
import org.junit.Test
import java.util.*

/**
 * Timer Tests
 *
 */
class TimerTest {

    @Test
    fun correctTimeComputationOnGivenDeadline() {
        val expectedTimeInMillis = 89_000L
        val fakeNow = 1998_000L
        val fakeCurrentTimeGetter = { fakeNow }
        val deadline = Date(fakeNow + expectedTimeInMillis)
        val timer = Timer(deadline, fakeCurrentTimeGetter)
        assertEquals(expectedTimeInMillis / 1000, timer.time)
    }

    @Test
    fun setTimeHasCorrectBehavior() {
        val testTime = 100L
        val timer = Timer()
        timer.setTime(testTime)
        assertEquals(testTime, timer.time)
        assertEquals(0, timer.currentTimeInSeconds)
    }

    @Test
    fun setDeadlineHasCorrectBehavior() {
        val expectedTimeInMillis = 89_000L
        val fakeNow = 1998_000L
        val fakeCurrentTimeGetter = { fakeNow }
        val deadline = Date(fakeNow + expectedTimeInMillis)
        val timer = Timer()
        timer.setDeadline(deadline, fakeCurrentTimeGetter)
        assertEquals(expectedTimeInMillis / 1000, timer.time)
        assertEquals(0, timer.currentTimeInSeconds)
    }

    @Test
    fun timerCorrectBehaviorOnDone() {
        val timerTimeInSeconds = 1L
        val timer = Timer(timerTimeInSeconds)
        var done = false
        timer.setOnDoneListener { done = true }
        timer.start()
        // Not done until the en
        assertFalse(done)

        // Sleep the time of the timer + 1
        Thread.sleep(timerTimeInSeconds * 1000 + 1)

        // Check it's done
        assertTrue(done)
    }



    @Test
    fun timerCorrectBehaviorOnUpdate() {
        val timerTimeInSeconds = 4L
        val timer = Timer(timerTimeInSeconds)
        var counter = 0L

        // Set the update time to be slightly bigger thant one sec to correctly get the value
        timer.setOnUpdateListener(1_001L) {
            counter = it
        }

        timer.start()

        for (i in 0 until timerTimeInSeconds) {
            assertEquals(i, counter)

            // Sleep a little more than the update refresh rate to correctly check
            Thread.sleep(1_002L)
        }
    }

    @Test
    fun stopCorrectlyWorks() {
        val timerTimeInSeconds = 4L
        val timer = Timer(timerTimeInSeconds)
        timer.start()
        assertTrue(timer.isRunning)
        timer.stop()
        assertFalse(timer.isRunning)

    }

}