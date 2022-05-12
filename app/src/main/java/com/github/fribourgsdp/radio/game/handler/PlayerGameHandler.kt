package com.github.fribourgsdp.radio.game.handler

import android.content.Context
import android.util.Log
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.database.FirestoreDatabase
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.game.GameView
import com.github.fribourgsdp.radio.util.NOT_THE_SAME
import com.github.fribourgsdp.radio.util.StringComparisons
import com.google.firebase.firestore.DocumentSnapshot

class PlayerGameHandler(
    private val ctx: Context,
    private val gameID: Long,
    private val view: GameView,
    db: Database = FirestoreDatabase()
): GameHandler(ctx, view, db), GameView.OnPickListener {

    private var songToGuess: String? = null
    private var scores: HashMap<String, Long>? = null

    override fun linkToDatabase() {
        db.listenToGameUpdate(gameID, executeOnUpdate())
    }

    override fun handleSnapshot(snapshot: DocumentSnapshot?) {
        if (snapshot != null && snapshot.exists()) {
            val gameStillValid = snapshot.getBoolean("validity")!!
            scores = snapshot.get("scores") as HashMap<String, Long>
            if (snapshot.getBoolean("finished")!! || !gameStillValid) {
                view.gameOver(scores!!, gameStillValid)
                return
            }

            val singerName = snapshot.getString("singer")!!

            view.updateSinger(singerName)
            view.updateRound(snapshot.getLong("current_round")!!)

            // update the score
            view.displayPlayerScores(scores!!)

            // Get the picked song
            // It's not null when there is one.
            songToGuess = snapshot.getString("current_song")


            updateViewForPlayer(snapshot, singerName)

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
            view.displayError("Wrong answer")
        } else if (nbErrors != 0) {
            view.displayError("You're close!")
        } else {
            view.displaySong("You correctly guessed $guess")
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

        db.updateCurrentSongOfGame(gameID, song)
            .addOnSuccessListener(onSuccess)
            .addOnFailureListener {
                Log.e("PlayerGameHandler Error", "onPick: ${it.message}", it)
                view.displayError(ctx.getString(R.string.game_error))

                // retry
                db.updateCurrentSongOfGame(gameID, song)
                    .addOnSuccessListener(onSuccess)
                    .addOnFailureListener {
                        view.gameOver(scores, true)
                    }
            }
    }

    private fun displayLyrics(snapshot: DocumentSnapshot){
        val lyricsHashMap =
            snapshot.get("song_choices_lyrics")!! as Map<String, String>
        val lyrics = lyricsHashMap[songToGuess!!]
        view.displayLyrics(lyrics!!)
    }

    private fun chooseSong(snapshot: DocumentSnapshot){
        val choices = snapshot.get("song_choices")!! as ArrayList<String>
        view.chooseSong(choices, this)
    }

    private fun updateViewForPlayer(snapshot: DocumentSnapshot, singerName : String){
        val deadline = snapshot.getTimestamp("round_deadline")

        if (view.checkPlayer(singerName)) {
            if (songToGuess == null) {
                chooseSong(snapshot)
            } else {
                displayLyrics(snapshot)
                view.startTimer(deadline!!.toDate())
            }

        } else {
            if (songToGuess != null) {
                // The singer picked a song so the player can guess
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

    fun disableGame() {
        db.disableGame(gameID)
    }

    fun removeUserFromLobby(user: User) {
        db.removeUserFromLobby(gameID, user)
    }

    fun removePlayerFromGame(user: User) {
        db.removePlayerFromGame(gameID, user)
    }
}