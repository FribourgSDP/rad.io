package com.github.fribourgsdp.radio
import java.util.*
import java.util.Timer as JavaTimer

class Timer(private val period: Long) {

    constructor(deadline: Date, currentTimeInMillis: () -> Long): this((currentTimeInMillis() - deadline.time) * 1000)
    constructor(deadline: Date): this(deadline, System::currentTimeMillis)

    var isRunning = false
        private set

    var currentTimeInSeconds = period
        private set

    private val scheduler = JavaTimer()

    private val updateTimeTask = UpdateTimeTask()

    private val doneTask = DoneTask()
    private var doneListener: OnTimerDoneListener? = null

    private val updateTask = UpdateTask()
    private var updateListener: OnTimerUpdateListener? = null

    init {
        // Decrement the time every millisecond
        scheduler.scheduleAtFixedRate(updateTimeTask, 0L, 1000L)
    }

    fun start(delay: Long = 0L) {
        isRunning = true

        // Schedule the done time to the end of the period
        scheduler.schedule(doneTask, delay * 1000, period)
    }

    fun stop() {
        isRunning = false
        doneTask.cancel()
    }

    fun resume(delay: Long = 0L) {
        isRunning = true

        // Schedule the done time to the remaining of time
        scheduler.schedule(doneTask, delay * 1000, currentTimeInSeconds)
    }

    fun setOnDoneListener(listener: OnTimerDoneListener) {
        doneListener = listener
    }

    fun setOnUpdateListener(listener: OnTimerUpdateListener, refreshRate: Long) {
        updateListener = listener
        scheduler.scheduleAtFixedRate(updateTask, 0L, refreshRate)
    }

    interface OnTimerDoneListener {
        fun onDone()
    }

    interface OnTimerUpdateListener {
        fun onUpdate(timeInSeconds: Long)
    }

    private inner class UpdateTimeTask: TimerTask() {
        override fun run() {
            if (isRunning) {
                currentTimeInSeconds -= 1
            }
        }
    }

    private inner class DoneTask: TimerTask() {
        override fun run() {
            isRunning = false
            updateTimeTask.cancel()
            doneListener?.onDone()
        }
    }

    private inner class UpdateTask: TimerTask() {
        override fun run() {
            if (isRunning) {
                updateListener?.onUpdate(currentTimeInSeconds)
            }
        }
    }
}