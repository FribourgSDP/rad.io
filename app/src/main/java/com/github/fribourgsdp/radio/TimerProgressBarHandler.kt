package com.github.fribourgsdp.radio

import android.view.View
import android.widget.ProgressBar
import java.util.*

/**
 * An instance of a handler to link a [progress bar][ProgressBar] and a [timer][Timer] together.
 *
 *
 * @property timer the [timer] of the [handler][TimerProgressBarHandler].
 * @property progressBar the [progress bar][progressBar] of the [handler][TimerProgressBarHandler].
 */
class TimerProgressBarHandler(val timer: Timer, val progressBar: ProgressBar, timerListener: Timer.Listener): Timer.DeadlineHandler {

    init {
        timer.setListener(timerListener, 500L)
        progressBar.max = timer.time.toInt()
        progressBar.progress = 0
    }

    override fun startTimer(deadline: Date, delay: Long) {
        progressBar.max = timer.time.toInt()
        progressBar.progress = 0
        progressBar.visibility = View.VISIBLE
        timer.apply {
            setTime(deadline)
            start(delay)
        }
    }

    override fun stopTimer() {
        progressBar.max = timer.time.toInt()
        progressBar.progress = 0
        progressBar.visibility = View.INVISIBLE
        timer.stop()
    }
}