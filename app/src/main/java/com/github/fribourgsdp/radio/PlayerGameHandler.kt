package com.github.fribourgsdp.radio

import com.google.firebase.firestore.DocumentSnapshot

class PlayerGameHandler(private val gameID: Long, private val view: GameView, db: Database = FirestoreDatabase()): GameHandler(view, db) {

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
                val pickedSong = view.chooseSong(choices)

                db.updateCurrentSongOfGame(gameID, pickedSong)
                    .addOnSuccessListener {
                        view.displaySong(pickedSong)
                    }
                    .addOnFailureListener {
                        view.displayError("An error occurred")
                    }

            } else {
                view.displayGuessInput()
            }

        } else {
            view.displayError("An error occurred")
        }
    }
}