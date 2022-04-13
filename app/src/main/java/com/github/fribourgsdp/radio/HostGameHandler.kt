package com.github.fribourgsdp.radio

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import kotlin.streams.toList

class HostGameHandler(private val game: Game, private val view: GameView, db: Database = FirestoreDatabase()): GameHandler(view, db) {
    override fun handleSnapshot(snapshot: DocumentSnapshot?) {
        if (snapshot != null && snapshot.exists()) {
            val doneMap = snapshot.get("player_done_map")!! as HashMap<String, Boolean>

            // Check that all values are 'true' => does not contains 'false'
            val allDone = !doneMap.containsValue(false)

            if (allDone) {
                // when everybody is done, add the points
                val scoresOfRound = snapshot.get("scores_of_round")!! as HashMap<String, Int>
                game.addPoints(scoresOfRound)

                // update the game
                val updatesMap = createUpdatesMap()
                db.updateGame(game.id, updatesMap).addOnSuccessListener {
                    db.resetGameMetadata(
                        game.id,
                        updatesMap["singer"] as String
                    ).addOnFailureListener {
                        view.displayError("An error occurred.")
                    }
                }.addOnFailureListener {
                    view.displayError("An error occurred.")
                }
            }

        } else {
            view.displayError("An error occurred.")
        }
    }

    override fun linkToDatabase() {
        db.listenToGameMetadataUpdate(game.id, executeOnUpdate())
    }

    private fun createUpdatesMap(): Map<String, Any> {
        val nextChoices = game.getChoices(3).stream()
            .map { song -> song.name }
            .toList()

        val nextUser = game.getUserToPlay()

        return hashMapOf(
            "current_round" to game.currentRound,
            "current_song" to FieldValue.delete(),
            "singer" to nextUser,
            "song_choices" to nextChoices,
            "scores" to game.getAllScores()
        )
    }

}

