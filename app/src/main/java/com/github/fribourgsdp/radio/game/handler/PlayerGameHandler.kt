package com.github.fribourgsdp.radio.game.handler

import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.database.FirestoreDatabase
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.game.GameView
import com.github.fribourgsdp.radio.util.NOT_THE_SAME
import com.github.fribourgsdp.radio.util.StringComparisons
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ListenerRegistration

class PlayerGameHandler(
    private val gameID: Long,
    private val view: GameView,
    db: Database = FirestoreDatabase()
): GameHandler(view, db), GameView.OnPickListener {

    private var songToGuess: String? = null
    private var snapshotListenerRegistration: ListenerRegistration? = null

    override fun linkToDatabase() {
        snapshotListenerRegistration = db.listenToGameUpdate(gameID, executeOnUpdate())
    }

    override fun unlinkFromDatabase() {
        snapshotListenerRegistration?.remove()
    }

    override fun handleSnapshot(snapshot: DocumentSnapshot?) {
        if (snapshot != null && snapshot.exists()) {
            val gameStillValid = snapshot.getBoolean("validity")!!
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


            updateViewForPlayer(snapshot, singerName)

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