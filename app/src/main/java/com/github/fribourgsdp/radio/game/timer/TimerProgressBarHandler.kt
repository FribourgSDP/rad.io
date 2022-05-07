package com.github.fribourgsdp.radio.game.timer

import android.view.View
import android.widget.ProgressBar
import java.util.*

/**
 * An instance of a handler to link a [progress bar][ProgressBar] and a [timer][Timer] together.
 *
 * NB:
 *
 * The parameter [timerListener][Timer.Listener] is responsible to update the [progress bar][progressBar]
 * since the update needs to be done on the main ui thread.
 *
 * To know what is the current time, we can give a function currentTimeInMillis. By default, it's [System.currentTimeMillis]
 *
 * @property timer the [timer] of the [handler][TimerProgressBarHandler].
 * @property progressBar the [progress bar][progressBar] of the [handler][TimerProgressBarHandler].
 */
class TimerProgressBarHandler(val timer: Timer, val progressBar: ProgressBar, timerListener: Timer.Listener, private val currentTimeInMillis: () -> Long = System::currentTimeMillis):
    Timer.DeadlineHandler {

    init {
        timer.setListener(PROGRESS_REFRESH_RATE, timerListener)
        timer.time?.let { progressBar.max = it.toInt() }
        progressBar.progress = 0
    }

    override fun startTimer(deadline: Date) {
        progressBar.progress = 0
        progressBar.visibility = View.VISIBLE
        timer.apply {
            setDeadline(deadline, currentTimeInMillis)
            time?.let { progressBar.max = it.toInt() }
            start()
        }
    }

    override fun stopTimer() {
        progressBar.visibility = View.INVISIBLE
        timer.stop()
    }

    companion object {
        private const val PROGRESS_REFRESH_RATE = 500L
    }
}