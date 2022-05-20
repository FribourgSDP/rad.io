package com.github.fribourgsdp.radio.game

import com.github.fribourgsdp.radio.game.timer.Timer

interface GameView: Timer.DeadlineHandler {
    /**
     * Make the user of the view pick a song from the given [choices].
     * The pick is handler by the [listener].
     * @return the name of the chosen song.
     */
    fun chooseSong(choices: List<String>, listener: OnPickListener)

    /**
     * Update the view to display the current singer in the game.
     * Or modifies the text saying who's singing to be neutral
     * @param singerId the id of the singer
     */
    fun updateSinger(singerId: String)

    /**
     * Update the view to display the current round of the game.
     * @param currentRound the current round
     */
    fun updateRound(currentRound: Long)

    /**
     * Make the view display the song that is currently being sung.
     * @param songName the name of the song to display
     */
    fun displaySong(songName: String)

    /**
     * Update the view to display the input text view to guess a song.
     */
    fun displayGuessInput()

    /**
     * Display an error on the view.
     * @param errorMessage the error message to display
     */
    fun displayError(errorMessage: String)

    /**
     * Hide the error on the view.
     */
    fun hideError()

    /**
     * Check if the given [id] is the same as the player from the view.
     * @return whether the given [id] is the same as the player from the view.
     */
    fun checkPlayer(id: String): Boolean

    /**
     * Make the view display that the [singer] is currently picking a song.
     */
    fun displayWaitOnSinger(singer: String)

    /**
     * Display on the view the scores in the map [playerScores].
     */
    fun displayPlayerScores(playerScores: Map<String, Long>)

    /**
     * Handle the end of the game.
     * @param finalScores the scores when the game is over
     * @param hasCrashed whether the game finished by crashing or not
     */
    fun gameOver(finalScores: Map<String, Long>?, hasCrashed: Boolean = false)

    /**
     * Update the [lyrics] of the current song in the game
     */
    fun updateLyrics(lyrics : String)

    /**
     * An interface creating listeners able to handle songs pick in a [GameView].
     */
    interface OnPickListener {
        /**
         * Handle the given picked [song].
         */
        fun onPick(song: String)
    }

}