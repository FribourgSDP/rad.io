package com.github.fribourgsdp.radio.game.timer
import java.util.*
import java.util.Timer as JavaTimer

/**
 * An instance of a timer that runs for the given [time] in seconds.
 */
class Timer(time: Long? = null) {

    /**
     * An instance of a timer that runs until the given [deadline].
     * To know what is the current time, we can give a function [currentTimeInMillis], that returns a [Long].
     */
    constructor(deadline: Date, currentTimeInMillis: () -> Long): this(computeTimeFromDeadline(deadline, currentTimeInMillis))

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
    var currentTimeInSeconds = 0L
        private set

    /**
     * The total time of the timer in seconds
     */
    var time = time
        private set

    private var scheduler = JavaTimer()

    private var doneListener: OnTimerDoneListener? = null
    private var updateListener: OnTimerUpdateListener? = null
    private var updateListenerRefreshRate = 0L
    private var updateListenerDelay = 0L

    /**
     * Starts the timer
     */
    fun start() {
        isRunning = true

        // Schedule the done time to the end of the period
        time?.let {
            scheduler.apply {
                schedule(DoneTask(), it * 1000)

                // Decrement the time every second
                schedule(UpdateTimeTask(), 1_000L, 1_000L)

                // if there was an update listener -> reschedule it
                updateListener?.let {
                    schedule(UpdateTask(), updateListenerDelay, updateListenerRefreshRate)
                }
            }
        }
    }

    /**
     * Stops the timer
     */
    fun stop() {
        isRunning = false
        scheduler.apply {
            cancel()
            purge()
        }

        // since the scheduler was canceled -> rebuild it
        scheduler = JavaTimer()
    }
    
    /**
     * Set the [listener] to handle when the timer is done.
     */
    fun setOnDoneListener(listener: OnTimerDoneListener) {
        doneListener = listener
    }

    /**
     * Set the [listener] to handle the updates of the timer at the [refreshRate] in milliseconds.
     */
    fun setOnUpdateListener(refreshRate: Long, listener: OnTimerUpdateListener) {
        updateListener = listener
        updateListenerRefreshRate = refreshRate
    }

    /**
     * Set the [listener] to handle when the timer is done and on the updates with the given [refreshRate] in milliseconds.
     */
    fun setListener(refreshRate: Long, listener: Listener) {
        setOnDoneListener(listener)
        setOnUpdateListener(refreshRate, listener)
    }

    /**
     * Set the [time] in seconds.
     */
    fun setTime(time: Long) {
        this.time = time
        this.currentTimeInSeconds = 0L
    }

    /**
     * Set the [time] to be the time from now until the given [deadline].
     * To know what is the current time, we can give a function [currentTimeInMillis], that returns a [Long].
     */
    fun setDeadline(deadline: Date, currentTimeInMillis: () -> Long) {
        setTime(computeTimeFromDeadline(deadline, currentTimeInMillis))
    }

    /**
     * Set the [time] to be the time from now until the given [deadline].
     */
    fun setDeadline(deadline: Date) {
        setDeadline(deadline, System::currentTimeMillis)
    }

    /**
     * An interface creating listeners able to handle the end of a [Timer].
     */
    fun interface OnTimerDoneListener {
        /**
         * Handle the behavior when the [Timer] is done.
         */
        fun onDone()
    }

    /**
     * An interface creating listeners able to handle the update of a [Timer].
     */
    fun interface OnTimerUpdateListener {
        /**
         * Handle the behavior on an update of the [Timer].
         * @param timeInSeconds the [time in seconds][Long] of the [Timer] at the moment of the update.
         */
        fun onUpdate(timeInSeconds: Long)
    }

    /**
     * An interface creating listeners able to handle both the update and the end of a [Timer].
     */
    interface Listener: OnTimerUpdateListener, OnTimerDoneListener

    /**
     * An interface creating handlers for a [Timer] with a [deadline][Date].
     */
    interface DeadlineHandler {

        /**
         * Start a timer
         * @param deadline the deadline of the timer
         */
        fun startTimer(deadline: Date)

        /**
         * Stop a timer
         */
        fun stopTimer()
    }

    private inner class UpdateTimeTask: TimerTask() {
        override fun run() {
            if (isRunning) {
                ++currentTimeInSeconds
            }
        }
    }

    private inner class DoneTask: TimerTask() {
        override fun run() {
            doneListener?.onDone()

            // Add 1 to currentTimeInSeconds to display the final time
            // This isn't done in the UpdateTask as it count's from zero to time - 1
            ++currentTimeInSeconds

            stop()
        }
    }

    private inner class UpdateTask: TimerTask() {
        override fun run() {
            if (isRunning) {
                updateListener?.onUpdate(currentTimeInSeconds)
            }
        }
    }

    companion object {
        private fun computeTimeFromDeadline(deadline: Date, currentTimeInMillis: () -> Long): Long {
            return (deadline.time - currentTimeInMillis()) / 1000
        }

        // The update refresh rate is of 1sec since the timer displays seconds
        private const val UPDATE_REFRESH_RATE = 1_000L
    }
}