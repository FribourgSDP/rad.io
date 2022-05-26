package com.github.fribourgsdp.radio.game.handler

import android.content.Context
import android.util.Log
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.database.FirestoreDatabase
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.game.GameView
import com.github.fribourgsdp.radio.game.prep.DEFAULT_GAME_DURATION
import com.github.fribourgsdp.radio.game.timer.Timer
import com.github.fribourgsdp.radio.util.MyTextToSpeech
import com.github.fribourgsdp.radio.util.NOT_THE_SAME
import com.github.fribourgsdp.radio.util.SongNameHint
import com.github.fribourgsdp.radio.util.StringComparisons
import com.github.fribourgsdp.radio.util.getAndCast
import com.google.firebase.firestore.DocumentSnapshot

class PlayerGameHandler(
    private val ctx: Context,
    private val gameID: Long,
    private val view: GameView,
    db: Database = FirestoreDatabase(),
    private val noSing : Boolean = false,
    private val tts : MyTextToSpeech? = null
): GameHandler(ctx, view, db), GameView.OnPickListener {

    private var songToGuess: String? = null
    private var scores: HashMap<String, Long>? = null
    private var singerDuration: Long = DEFAULT_GAME_DURATION
    private val stopTimer = Timer(singerDuration + WAIT_DELTA_IN_SECONDS).apply {
        // When this timer expires, stop the game
        setOnDoneListener { view.gameOver(scores, true) }
    }

    override fun linkToDatabase() {
        db.listenToGameUpdate(gameID, executeOnUpdate())
    }

    override fun handleSnapshot(snapshot: DocumentSnapshot?) {
        if (snapshot != null && snapshot.exists()) {
            // stop the timer if it was running
            stopTimer.stop()

            val gameStillValid = snapshot.getBoolean("validity")!!
            scores = snapshot.get("scores") as HashMap<String, Long>
            if (snapshot.getBoolean("finished")!! || !gameStillValid) {
                view.gameOver(scores!!, !gameStillValid)
                return
            }

            view.updateRound(snapshot.getLong("current_round")!!)

            // update the score
            view.displayPlayerScores(scores!!)

            // Get the picked song
            // It's not null when there is one.
            songToGuess = snapshot.getString("current_song")

            if(!noSing) {
                val singerName = snapshot.getString("singer")!!
                view.updateSinger(singerName)
                updateViewForPlayer(snapshot, singerName)
            } else{
                updateViewNoSingMode(snapshot)
            }

            // Start the stop timer to stop everything if something fails
            stopTimer.start()

        } else {
            Log.e("PlayerGameHandler Error", "Snapshot error")
            view.displayError(ctx.getString(R.string.game_error))
        }
    }

    fun handleGuess(guess: String, userId: String, timeout: Boolean = false) {
        if (songToGuess == null) { return }

        if (timeout) {
            handleTimeoutOnGuess(userId)
            // exit the handling when timeout
            return
        }

        val nbErrors = StringComparisons.compare(songToGuess!!, guess)
        if (nbErrors == NOT_THE_SAME) {
            view.displayError(ctx.getString(R.string.wrong_guess))
        } else if (nbErrors != 0) {
            view.displayError(ctx.getString(R.string.close_guess))
        } else {
            view.displaySong(ctx.getString(R.string.correct_guess_format, songToGuess))
            view.stopTimer()

            // Hide the error if a wrong guess was made
            view.hideError()

            db.playerEndTurn(gameID, userId, true)
                .addOnFailureListener {
                    Log.e("PlayerGameHandler Error", "In end turn: ${it.message}", it)
                    view.displayError(ctx.getString(R.string.game_error))

                    // retry
                    db.playerEndTurn(gameID, userId, true)
                        .addOnFailureListener {
                            view.gameOver(scores, true)
                        }
                }
        }
    }

    private fun handleTimeoutOnGuess(userId: String) {
        if(noSing){
            view.displaySong(ctx.getString(R.string.previousSongDisplay) +songToGuess.toString())
        }
        db.playerEndTurn(gameID, userId, false)
            .addOnFailureListener {
                Log.e("PlayerGameHandler Error", "In end turn with timeout: ${it.message}", it)
                view.displayError(ctx.getString(R.string.game_error))

                // retry
                db.playerEndTurn(gameID, userId, false)
                    .addOnFailureListener {
                        view.gameOver(scores, true)
                    }
            }

        // Hide the error if a wrong guess was made
        view.hideError()
    }

    override fun onPick(song: String) {
        val onSuccess: (Void?) -> Unit = { view.displaySong(song) }

        db.updateCurrentSongOfGame(gameID, song, singerDuration)
            .addOnSuccessListener(onSuccess)
            .addOnFailureListener {
                Log.e("PlayerGameHandler Error", "onPick: ${it.message}", it)
                view.displayError(ctx.getString(R.string.game_error))

                // retry
                db.updateCurrentSongOfGame(gameID, song, singerDuration)
                    .addOnSuccessListener(onSuccess)
                    .addOnFailureListener {
                        view.gameOver(scores, true)
                    }
            }
    }

    private fun updateLyrics(snapshot: DocumentSnapshot){
        view.updateLyrics(getLyricsFromSnapshot(snapshot))
    }


    private fun getLyricsFromSnapshot(snapshot: DocumentSnapshot) : String {
        val lyricsHashMap =
            snapshot.getAndCast<Map<String, String>>("song_choices_lyrics")
        return lyricsHashMap[songToGuess!!]!!
    }

    private fun chooseSong(snapshot: DocumentSnapshot){
        val choices = snapshot.getAndCast<ArrayList<String>>("song_choices")
        view.chooseSong(choices, this)
    }

    private fun updateViewNoSingMode(snapshot: DocumentSnapshot) {
        val deadline = snapshot.getTimestamp("round_deadline")
        if(songToGuess != null && deadline != null) {
            tts!!.readLyrics(getLyricsFromSnapshot(snapshot))
            view.startTimer(deadline.toDate())
        } else{
            view.stopTimer()
            view.updateSinger(NO_SINGER)
        }
    }
    private fun updateViewForPlayer(snapshot: DocumentSnapshot, singerName : String){
        val deadline = snapshot.getTimestamp("round_deadline")

        if (view.checkPlayer(singerName)) {
            if (songToGuess == null) {
                chooseSong(snapshot)
            } else {
                updateLyrics(snapshot)
                view.displaySong(songToGuess!!)
                view.startTimer(deadline!!.toDate())
            }

        } else {
            // No lyrics when user is not the singer
            view.updateLyrics("")

            if (songToGuess != null) {
                // The singer picked a song so the player can guess
                view.addHint(SongNameHint(songToGuess!!))
                view.displayGuessInput()
                view.startTimer(deadline!!.toDate())

            } else {
                // Stop the timer while waiting
                view.stopTimer()

                // The singer is till picking, so the player waits
                view.displayWaitOnSinger(singerName)
            }


        }
    }

    fun setSingerDuration(duration: Long) {
        singerDuration = duration
        stopTimer.setTime(duration + WAIT_DELTA_IN_SECONDS)
    }

    fun disableGame() {
        db.disableGame(gameID)
    }

    fun removeUserFromLobby(user: User) {
        db.removeUserFromLobby(gameID, user)
    }

    fun removePlayerFromGame(user: User) {
        db.removePlayerFromGame(gameID, user)
    }

    companion object {
        private const val WAIT_DELTA_IN_SECONDS = 5L
    }
}