package com.github.fribourgsdp.radio

import android.util.Log
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import kotlin.streams.toList

class HostGameHandler(private val game: Game, private val view: GameView, db: Database = FirestoreDatabase()): GameHandler(view, db) {
    private var latestSingerId: String? = null

    override fun handleSnapshot(snapshot: DocumentSnapshot?) {
        Log.println(Log.ASSERT, "*", "HANDLE SNAPSHOT Host")
        if (snapshot != null && snapshot.exists()) {
            val doneMap = snapshot.getAndCast<HashMap<String, Boolean>>("player_done_map")

            // Check that all values are 'true' => does not contains 'false'
            val allDone = !doneMap.containsValue(false)

            if (allDone) {
                // when everybody is done, add the points
                val scoresOfRound = snapshot.getAndCast<HashMap<String, Long>>("scores_of_round")
                game.addPoints(scoresOfRound)

                // then update the points of the singer
                val playerFoundMap = snapshot.getAndCast<HashMap<String, Boolean>>("player_found_map")

                // Count the number of players that found the answer
                // We then multiply by the number of points the singer gets
                val singerPoints = playerFoundMap.count { (_, hasFound) -> hasFound } * NB_POINTS_PER_PLAYER_FOUND

                // On the first run, the singer id will be null, so we don't update the map
                latestSingerId?.let { game.addPoints(it, singerPoints) }

                // update the game
                val updatesMap = createUpdatesMap()
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

    private fun createUpdatesMap(): Map<String, Any> {
        val done = game.isDone()
        val nextChoices = MutableList<String>(0) { "" }
        val nextChoicesLyrics = HashMap<String, String>(3)
        game.getChoices(3).stream()
            .forEach { song ->
                nextChoices.add(song.name)
                nextChoicesLyrics[song.name] = song.lyrics
            }

        val nextUser = game.getUserToPlay()
        Log.println(Log.ASSERT, "*", nextChoices.toString())

        return hashMapOf(
            "finished" to done,
            "current_round" to game.currentRound,
            "current_song" to FieldValue.delete(),
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

