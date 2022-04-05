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

            if (view.checkPlayer(singerName)) {
                val choices = snapshot.get("song_choices")!! as ArrayList<String>
                view.chooseSong(choices, this)

            } else {
                // Get the picked song
                // It's not null when there is one.
                songToGuess = snapshot.getString("current_song")

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

    fun handleGuess(guess: String, username: String) {
        // TODO: In a later update, use a point system
        if (songToGuess != null && songToGuess!!.lowercase() == guess.lowercase()) {

            db.setPlayerDone(gameID, username).addOnFailureListener {
                view.displayError("An error occurred")
            }

            view.displaySong(guess)
        } else if (songToGuess != null) {
            view.displayError("Wrong answer")
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