package com.github.fribourgsdp.radio
import java.util.*
import java.util.Timer as JavaTimer

/**
 * An instance of a timer that runs for the given [time].
 */
class Timer(time: Long = 0L) {

    /**
     * An instance of a timer that runs until the given [deadline].
     * To know what is the current time, we can give a function [currentTimeInMillis] that returns a [Long].
     */
    constructor(deadline: Date, currentTimeInMillis: () -> Long): this((currentTimeInMillis() - deadline.time) * 1000)

    /**
     * An instance of a timer that runs until the given [deadline].
     */
    constructor(deadline: Date): this(deadline, System::currentTimeMillis)

    /**
     * Whether the timer is running or not
     */
    var isRunning = false
        private set

    /**
     * The current time of the timer in seconds
     */
    var currentTimeInSeconds = time
        private set

    /**
     * The total time of the timer in seconds
     */
    var time = time
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

    /**
     * Starts the timer
     * @param delay an optional delay to the start of the timer. By default: 0
     */
    fun start(delay: Long = 0L) {
        isRunning = true

        // Schedule the done time to the end of the period
        scheduler.schedule(doneTask, delay * 1000, time)
    }

    /**
     * Stops the timer
     */
    fun stop() {
        isRunning = false
        doneTask.cancel()
    }

    fun setOnDoneListener(listener: OnTimerDoneListener) {
        doneListener = listener
    }

    fun setOnUpdateListener(listener: OnTimerUpdateListener, refreshRate: Long) {
        updateListener = listener
        scheduler.scheduleAtFixedRate(updateTask, 0L, refreshRate)
    }

    fun setTime(time: Long) {
        this.time = time
    }

    fun setTime(deadline: Date, currentTimeInMillis: () -> Long) {
        setTime((currentTimeInMillis() - deadline.time) * 1000)
    }

    fun setTime(deadline: Date) {
        setTime(deadline, System::currentTimeMillis)
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