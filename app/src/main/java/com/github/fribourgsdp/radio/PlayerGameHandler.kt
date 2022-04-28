package com.github.fribourgsdp.radio

import com.google.firebase.firestore.DocumentSnapshot

class PlayerGameHandler(
    private val gameID: Long,
    private val view: GameView,
    db: Database = FirestoreDatabase()
): GameHandler(view, db), GameView.OnPickListener {

    private var songToGuess: String? = null

    override fun linkToDatabase() {
        db.listenToGameUpdate(gameID, executeOnUpdate())
    }

    override fun handleSnapshot(snapshot: DocumentSnapshot?) {
        if (snapshot != null && snapshot.exists()) {
            val scores = snapshot.get("scores") as HashMap<String, Long>
            if (snapshot.getBoolean("finished")!!) {
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
                    val deadline = snapshot.getTimestamp("round_deadline")!!

                    // The singer picked a song so the player can guess
                    view.displayGuessInput()
                    view.startTimer(deadline.toDate())

                } else {
                    // The singer is till picking, so the player waits
                    view.displayWaitOnSinger(singerName)
                }


            }

        } else {
            view.displayError("An error occurred")
        }
    }

    fun handleGuess(guess: String, userId: String, timeout: Boolean = false) {
        if (songToGuess == null) { return }

        if (timeout) {
            db.playerEndTurn(gameID, userId, false).addOnFailureListener {
                view.displayError("An error occurred")
            }

            // Hide the error if a wrong guess was made
            view.hideError()

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