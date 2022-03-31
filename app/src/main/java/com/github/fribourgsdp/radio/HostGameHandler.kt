package com.github.fribourgsdp.radio

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener
import com.google.firebase.firestore.FieldValue
import kotlin.streams.toList

class HostGameHandler(private val game: Game, private val view: GameView, db: Database = FirestoreDatabase()): GameHandler(view, db) {
    override fun handleSnapshot(snapshot: DocumentSnapshot?) {
        if (snapshot != null && snapshot.exists()) {
            val doneMap = snapshot.get("player_done_map") as HashMap<String, Boolean>

            // Check that all values are 'true' => does not contains 'false'
            val allDone = !doneMap.containsValue(false)

            if (allDone) {
                val updatesMap = createUpdatesMap()

                // When everybody is done, change update the game
                db.updateGame(game.id, updatesMap).addOnSuccessListener {
                    db.resetPlayerDoneMap(
                        (updatesMap["singer"] as User).name
                    ).addOnFailureListener {
                        view.displayError("An error occurred.")
                    }
                }.addOnFailureListener {
                    view.displayError("An error occurred.")
                }
            }

        }
    }

    override fun linkToDatabase() {
        db.listenToGameMetadataUpdate(game.id, executeOnUpdate())
    }

    private fun createUpdatesMap(): Map<String, Any> {
        val nextChoices = game.getChoices(3).stream()
            .map { song -> song.name }
            .toList()

        return hashMapOf(
            "current_round" to game.currentRound,
            "current_song" to FieldValue.delete(),
            "singer" to game.getUserToPlay(),
            "song_choices" to nextChoices
        )
    }

}

