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

    override fun chooseSong(choices: List<String>): String {
        return choices[0]
    }

    override fun updateSinger(singerName: String) {
        singer = singerName
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

}