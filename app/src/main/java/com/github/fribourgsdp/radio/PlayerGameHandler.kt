package com.github.fribourgsdp.radio

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener

class PlayerGameHandler(private val game: Game, private val view: GameView): GameHandler(game, view) {
    override fun executeOnUpdate(): EventListener<DocumentSnapshot> {
        return EventListener<DocumentSnapshot> { snapshot, e ->
            if (e != null) {
                view.displayError("An error occurred")
            }

            if (snapshot != null && snapshot.exists()) {
                view.updateSinger(snapshot.getString("singer")!!)
                view.updateRound(snapshot.getLong("current_round")!!)

                if (view.checkPlayer(snapshot.getString("singer")!!)) {
                    val choices = snapshot.get("song_choices")!! as ArrayList<String>
                    val pickedSong = view.chooseSong(choices)

                    db.updateCurrentSongOfGame(game.id, pickedSong)
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
}