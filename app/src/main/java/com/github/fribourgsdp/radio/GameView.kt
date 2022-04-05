package com.github.fribourgsdp.radio

interface GameView {
    /**
     * Make the user of the view pick a song from the given [choices].
     * The pick is handler by the [listener].
     * @return the name of the chosen song.
     */
    fun chooseSong(choices: List<String>, listener: OnPickListener)

    /**
     * Update the view to display the current singer in the game.
     */
    fun updateSinger(singerName: String)

    /**
     * Update the view to display the current round of the game.
     */
    fun updateRound(currentRound: Long)

    /**
     * Make the view display the song that is currently being sung.
     */
    fun displaySong(songName: String)

    /**
     * Update the view to display the input text view to guess a song.
     */
    fun displayGuessInput()

    /**
     * Display an error on the view.
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
     * An interface creating listeners able to handle songs pick in a [GameView].
     */
    interface OnPickListener {
        /**
         * Handle the given picked [song].
         */
        fun onPick(song: String)
    }
}