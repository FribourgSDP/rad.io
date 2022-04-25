package com.github.fribourgsdp.radio

import android.content.Intent
import com.google.firebase.firestore.DocumentSnapshot

class PlayerGameHandler(
    val gameID: Long,
    private val view: GameView,
    db: Database = FirestoreDatabase()
): GameHandler(view, db), GameView.OnPickListener {

    val database = db

    private var songToGuess: String? = null

    override fun linkToDatabase() {
        db.listenToGameUpdate(gameID, executeOnUpdate())
    }

    override fun handleSnapshot(snapshot: DocumentSnapshot?) {
        if (snapshot != null && snapshot.exists()) {
            val gameStillValid = snapshot.get("validity") as Boolean
            val scores = snapshot.get("scores") as HashMap<String, Long>
            if (snapshot.getBoolean("finished")!! || !gameStillValid) {
                view.gameOver(scores)
                return
            }

            val singerName = snapshot.getString("singer")!!

            view.updateSinger(singerName)
            view.updateRound(snapshot.getLong("current_round")!!)

            // update the score
            view.displayPlayerScores(scores)

            // Get the picked song
            // It's not null when there is one.
            songToGuess = snapshot.getString("current_song")

            if (view.checkPlayer(singerName)) {
                if (songToGuess == null) {
                    val choices = snapshot.get("song_choices")!! as ArrayList<String>
                    view.chooseSong(choices, this)
                }

            } else {
                if (songToGuess != null) {
                    // The singer picked a song so the player can guess
                    view.displayGuessInput()
                } else {
                    // The singer is till picking, so the player waits
                    view.displayWaitOnSinger(singerName)
                }


            }

        } else {
            view.displayError("An error occurred")
        }
    }

    fun handleGuess(guess: String, userId: String) {
        if (songToGuess == null) { return }

        val nbErrors = StringComparisons.compare(songToGuess!!, guess)
        if (nbErrors == NOT_THE_SAME) {
            view.displayError("Wrong answer")
        } else if (nbErrors != 0) {
            view.displayError("You're close!")
        } else {
            view.displaySong("You correctly guessed $guess")

            // Hide the error if a wrong guess was made
            view.hideError()

            db.playerEndTurn(gameID, userId, true).addOnFailureListener {
                    view.displayError("An error occurred")
                }
        }
    }

    override fun onPick(song: String) {
        db.updateCurrentSongOfGame(gameID, song)
            .addOnSuccessListener {
                view.displaySong(song)
            }
            .addOnFailureListener {
                view.displayError("An error occurred")
            }
    }



}