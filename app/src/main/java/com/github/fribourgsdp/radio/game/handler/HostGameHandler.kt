package com.github.fribourgsdp.radio.game.handler

import android.content.Context
import android.util.Log
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.database.FirestoreDatabase
import com.github.fribourgsdp.radio.external.musixmatch.LyricsGetter
import com.github.fribourgsdp.radio.external.musixmatch.MusixmatchLyricsGetter
import com.github.fribourgsdp.radio.game.Game
import com.github.fribourgsdp.radio.game.GameView
import com.github.fribourgsdp.radio.game.prep.DEFAULT_SINGER_DURATION
import com.github.fribourgsdp.radio.util.getPlayerDoneMap
import com.github.fribourgsdp.radio.util.getPlayerFoundMap
import com.github.fribourgsdp.radio.util.getScoresOfRound
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue

const val NO_SINGER = ""

class HostGameHandler(
    private val ctx: Context,
    private val game: Game,
    private val view: GameView,
    db: Database = FirestoreDatabase(),
    private val noSing : Boolean = false,
    private val lyricsGetter: LyricsGetter = MusixmatchLyricsGetter
): GameHandler(ctx, view, db) {
    private var latestSingerId: String? = null
    private var singerDuration : Long = DEFAULT_SINGER_DURATION

    override fun handleSnapshot(snapshot: DocumentSnapshot?) {
        if (snapshot != null && snapshot.exists()) {
            val doneMap = snapshot.getPlayerDoneMap()

            // Check that all values are 'true' => does not contains 'false'
            val allDone = !doneMap.containsValue(false)

            //Check if only the host is left
            if (doneMap.size <= 1 && !noSing) {
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
                val onSuccess: (Void?) -> (Unit) = {
                    if(!noSing) {
                        // update the latest singer
                        latestSingerId = updatesMap["singer"] as String
                        resetGameMetadata(latestSingerId!!)
                    } else{
                        resetGameMetadata(NO_SINGER)
                        assignSong(updatesMap["current_song"] as String)
                        game.incrementCurrentRound()
                    }
                }

                db.updateGame(game.id, updatesMap)
                    .addOnSuccessListener(onSuccess)
                    .addOnFailureListener {
                        Log.e("HostGameHandler Error", "Game update: ${it.message}", it)
                        view.displayError(ctx.getString(R.string.game_error))

                        // retry
                        db.updateGame(game.id, updatesMap)
                            .addOnSuccessListener(onSuccess)
                            .addOnFailureListener {
                                // quit on second failure
                                view.gameOver(game.getAllScores(), true)
                            }
                }
            }

        } else {
            Log.e("HostGameHandler Error", "Snapshot error")
            view.displayError(ctx.getString(R.string.game_error))
        }
    }

    private fun resetGameMetadata(latestSingerId: String) {
        db.resetGameMetadata(
            game.id,
            latestSingerId
        ).addOnFailureListener {
            Log.e("HostGameHandler Error", "Metadata reset: ${it.message}", it)
            view.displayError(ctx.getString(R.string.game_error))

            // retry
            db.resetGameMetadata(
                game.id,
                latestSingerId
            ).addOnFailureListener {
                // quit on second failure
                view.gameOver(game.getAllScores(), true)
            }
        }
    }

    private fun assignSong(songName : String){
        db.updateCurrentSongOfGame(game.id, songName, singerDuration)
            .addOnFailureListener {
                Log.e("HostGameHandler Error", "Song assignment: ${it.message}", it)
                view.displayError(ctx.getString(R.string.game_error))

                db.updateCurrentSongOfGame(game.id, songName, singerDuration)
                    .addOnFailureListener{
                        view.gameOver(game.getAllScores(), true)
                    }
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

        if(!noSing){
            game.getChoices(3).stream()
                .forEach { song ->
                    nextChoices.add(song.name)
                    nextChoicesLyrics[song.name] = lyricsGetter.markSongName(song.lyrics, song.name)
                }
        } else {
            game.getChoiceWithLyrics().let {
                nextChoices.add(it!!.name)
                nextChoicesLyrics[it.name] = lyricsGetter.markSongName(it.lyrics, it.name)
            }
        }

        val updatesMap = hashMapOf(
            "finished" to done,
            "current_round" to game.currentRound,
            "round_deadline" to FieldValue.delete(),
            "song_choices" to nextChoices.toList(),
            "song_choices_lyrics" to nextChoicesLyrics,
            "scores" to game.getAllScores()
        )

        return if(!noSing) {
            var nextUser = ""
            do {
                nextUser = game.getUserToPlay()
            } while (!playerIdsOnDatabase.contains(nextUser))
            updatesMap + ("current_song" to FieldValue.delete()) + ("singer" to nextUser)
        } else{
            updatesMap + ("current_song" to nextChoices[0])
        }
    }

    fun setSingerDuration(l : Long){
        singerDuration = l
    }

    companion object {
        private const val NB_POINTS_PER_PLAYER_FOUND = 50L
    }

}

