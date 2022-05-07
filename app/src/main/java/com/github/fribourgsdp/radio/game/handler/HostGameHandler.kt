package com.github.fribourgsdp.radio.game.handler

import android.util.Log
import com.github.fribourgsdp.radio.*
import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.database.FirestoreDatabase
import com.github.fribourgsdp.radio.game.Game
import com.github.fribourgsdp.radio.game.GameView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import java.lang.IllegalStateException

class HostGameHandler(private val game: Game, private val view: GameView, db: Database = FirestoreDatabase()): GameHandler(view, db) {
    private var latestSingerId: String? = null

    override fun handleSnapshot(snapshot: DocumentSnapshot?) {
        Log.println(Log.ASSERT, "*", "HANDLE SNAPSHOT Host")
        if (snapshot != null && snapshot.exists()) {
            val doneMap = snapshot.getPlayerDoneMap()

            // Check that all values are 'true' => does not contains 'false'
            val allDone = !doneMap.containsValue(false)

            //Check if only the host is left
            if (doneMap.size <= 1) {
                view.gameOver(game.getAllScores())
                return
            }

            if (allDone) {
                // when everybody is done, add the points
                val scoresOfRound = snapshot.getScoresOfRound<Long>()
                game.addPoints(scoresOfRound)

                // then update the points of the singer
                val playerFoundMap = snapshot.getPlayerFoundMap()

                // Count the number of players that found the answer
                // We then multiply by the number of points the singer gets
                val singerPoints = playerFoundMap.count { (_, hasFound) -> hasFound } * NB_POINTS_PER_PLAYER_FOUND

                // On the first run, the singer id will be null, so we don't update the map
                latestSingerId?.let { game.addPoints(it, singerPoints) }

                // update the game
                val updatesMap = createUpdatesMap(doneMap.keys)
                db.updateGame(game.id, updatesMap).addOnSuccessListener {
                    // update the latest singer
                    latestSingerId = updatesMap["singer"] as String
                    db.resetGameMetadata(
                        game.id,
                        latestSingerId!!
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

    private fun createUpdatesMap(playerIdsOnDatabase: Set<String>): Map<String, Any> {
        if (playerIdsOnDatabase.isEmpty()) {
            throw IllegalStateException()
        }
        val done = game.isDone()
        val nextChoices = MutableList<String>(0) { "" }
        val nextChoicesLyrics = HashMap<String, String>(3)
        game.getChoices(3).stream()
            .forEach { song ->
                nextChoices.add(song.name)
                nextChoicesLyrics[song.name] = song.lyrics
            }

        var nextUser = ""
        do {
            nextUser = game.getUserToPlay()
        } while (!playerIdsOnDatabase.contains(nextUser))


        return hashMapOf(
            "finished" to done,
            "current_round" to game.currentRound,
            "current_song" to FieldValue.delete(),
            "round_deadline" to FieldValue.delete(),
            "singer" to nextUser,
            "song_choices" to nextChoices.toList(),
            "song_choices_lyrics" to nextChoicesLyrics,
            "scores" to game.getAllScores()
        )
    }

    companion object {
        private const val NB_POINTS_PER_PLAYER_FOUND = 50L
    }

}
