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
            val singerName = snapshot.getString("singer")!!

            view.updateSinger(singerName)
            view.updateRound(snapshot.getLong("current_round")!!)

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
        if (songToGuess == null) {
            return
        }

        val nbErrors = StringComparisons.compare(songToGuess!!, guess)

        if (nbErrors == NOT_THE_SAME) {
            view.displayError("Wrong answer")
        } else if (nbErrors != 0) {
            view.displayError("You're close!")
        } else {
            view.displaySong("You guessed $guess")

            // Hide the error if a wrong guess was made
            view.hideError()

            db.setPlayerDone(gameID, userId)
                .addOnSuccessListener {

                    db.getPositionInGame(gameID, userId)
                        .addOnSuccessListener { position ->

                            db.addPointsToPlayer(gameID, userId, Game.computeScore(position)).addOnFailureListener {
                                view.displayError("An error occurred")
                            }

                        }.addOnFailureListener {
                            view.displayError("An error occurred")
                        }

                }.addOnFailureListener {
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