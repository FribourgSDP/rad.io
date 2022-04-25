package com.github.fribourgsdp.radio.mockimplementations

import android.view.View
import com.github.fribourgsdp.radio.GameView

class FakeGameView(private val playerID: String = ""): GameView {

    var singer = ""
    var round = 0L

    var song = ""
    var songVisibility = View.GONE

    var error = ""
    var errorVisibility= View.GONE

    var guessInputVisibility = View.VISIBLE

    var scores: Map<String, Long> = HashMap()

    var gameOver = false

    override fun chooseSong(choices: List<String>, listener: GameView.OnPickListener) {
        listener.onPick(choices[0])
    }

    override fun updateSinger(singerId: String) {
        singer = singerId
    }

    override fun updateRound(currentRound: Long) {
        round = currentRound
    }

    override fun displaySong(songName: String) {
        guessInputVisibility = View.GONE
        song = songName
        songVisibility = View.VISIBLE
    }

    override fun displayGuessInput() {
        songVisibility = View.GONE
        guessInputVisibility = View.VISIBLE
    }

    override fun displayError(errorMessage: String) {
        error = errorMessage
        errorVisibility = View.VISIBLE
    }

    override fun hideError() {
        errorVisibility = View.GONE
    }

    override fun checkPlayer(id: String): Boolean {
        return playerID == id
    }

    override fun displayWaitOnSinger(singer: String) {
        displaySong(singer)
    }

    override fun displayPlayerScores(playerScores: Map<String, Long>) {
        scores = HashMap(playerScores)
    }

    override fun gameOver(finalScores: Map<String, Long>) {
        gameOver = true
    }

    override fun returnToMainMenu() {
        TODO("Not yet implemented")
    }

}