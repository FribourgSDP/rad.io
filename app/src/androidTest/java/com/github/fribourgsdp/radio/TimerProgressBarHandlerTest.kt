package com.github.fribourgsdp.radio

import org.junit.Assert.*
import android.content.Context
import android.view.View
import android.widget.ProgressBar
import androidx.test.core.app.ApplicationProvider
import com.github.fribourgsdp.radio.game.timer.Timer
import com.github.fribourgsdp.radio.game.timer.TimerProgressBarHandler
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.*

/**
 * Timer ProgressBar Handler Tests
 *
 */
class TimerProgressBarHandlerTest {
    private lateinit var handler: TimerProgressBarHandler
    private val ctx: Context = ApplicationProvider.getApplicationContext()

    private val testTimerTime = 1998_000L
    private val testCurrentTimeGetter = { testTimerTime }

    @Before
    fun setup() {
        handler = TimerProgressBarHandler(Timer(testTimerTime), ProgressBar(ctx), MockTimerListener(), testCurrentTimeGetter)
    }

    @After
    fun tearDown() {
        // Stop the timer in case it was still running
        handler.stopTimer()
    }

    @Test
    fun initWorksCorrectly() {
        assertEquals(0, handler.progressBar.progress)
        assertEquals(testTimerTime, handler.progressBar.max.toLong())
    }

    @Test
    fun startWorksAsIntended() {
        val expectedTimeInMillis = 89_000L
        val testDeadline = Date(testTimerTime + expectedTimeInMillis)

        handler.startTimer(testDeadline)

        assertEquals(0, handler.progressBar.progress)
        assertEquals(View.VISIBLE, handler.progressBar.visibility)
        assertEquals(expectedTimeInMillis / 1000, handler.progressBar.max.toLong())
        assertTrue(handler.timer.isRunning)
    }

    @Test
    fun stopWorksAsIntended() {
        handler.stopTimer()

        assertEquals(View.INVISIBLE, handler.progressBar.visibility)
        assertFalse(handler.timer.isRunning)
    }

    private class MockTimerListener: Timer.Listener {
        override fun onUpdate(timeInSeconds: Long) {
            // DO NOTHING
        }

        override fun onDone() {
            // DO NOTHING
        }

    }

}