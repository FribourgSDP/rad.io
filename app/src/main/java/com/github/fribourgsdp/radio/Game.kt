package com.github.fribourgsdp.radio

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet
import kotlinx.serialization.Serializable
import kotlin.math.min

/**
 * An instance of a game.
 *
 *
 * @property name the name of the game.
 * @property host the host of the game.
 * @property playlist the playlist used during the game.
 * @property nbRounds the number of rounds of a game.
 * @property withHint whether there is hints or not during the game.
 * @property isPrivate whether the game is private or public.
 * @property currentRound the current round of the game.
 */
@Serializable
class Game private constructor(val id: Long, val name: String, val host: User, val playlist: Playlist, val nbRounds: Int,
                               val withHint: Boolean, val isPrivate: Boolean, private val listUser: List<String>) {

    private val scoreMap = HashMap(listUser.associateWith { 0L })
    private var usersToPlay = listUser.size

    var currentRound = 1
        private set

    private val songsNotDone = HashSet(playlist.getSongs())

    /**
     * Return the score of a user.
     * @param userId the id of the user
     * @return the score of a user.
     */
    fun getScore(userId: String): Long? {
        return scoreMap[userId]
    }

    /**
     * Return the scores of all users.
     * @return the scores of all users.
     */
    fun getAllScores(): Map<String, Long> {
        return HashMap(scoreMap)
    }

    /**
     * Add the given number of [points] to the given user with id: [userId].
     */
    fun addPoints(userId: String, points: Long) {
        val oldValue = scoreMap[userId]
        if (oldValue != null) {
            scoreMap[userId] = oldValue + points
        } else {
            throw IllegalArgumentException("Illegal argument: user id: '$userId' doesn't exist.")
        }

    }

    /**
     * Add the given number of points to each user in [pointsMap].
     */
    fun addPoints(pointsMap: Map<String, Long>) {
        pointsMap.forEach(this::addPoints)
    }

    /**
     * Return the id of the user that is playing next.
     * @return the id of the user that is playing next.
     */
    fun getUserToPlay(): String {
        val userId = listUser[0]
        Collections.rotate(listUser, 1)
        if (usersToPlay == 0) {
            ++currentRound
            usersToPlay = listUser.size
        }
        --usersToPlay

        return userId
    }

    /**
     * Return a certain number ([nb]) of random songs. All songs from consecutive calls are different until they are all done.
     * It returns a smaller subset if the asked number is bigger than the number of songs left.
     * @return a random subset of songs.
     */
    fun getChoices(nb: Int): List<Song> {
        // Check if all the songs have been done and restart if so
        if (songsNotDone.isEmpty()) {
            songsNotDone.addAll(playlist.getSongs())
        }

        val nbRandom = if (songsNotDone.size < nb) songsNotDone.size else nb

        val chosen = ArrayList<Song>()

        for (i in 1..nbRandom) {
            val random = songsNotDone.random()
            chosen.add(random)
            songsNotDone.remove(random)
        }

        return chosen
    }

    /**
     * Return whether the game is done or not.
     * @return whether the game is done or not.
     */
    fun isDone(): Boolean {
        return nbRounds <= currentRound && usersToPlay <= 0
    }

    fun getAllPlayersId(): List<String> {
        return ArrayList(listUser)
    }

    /**
     * A builder for the [Game] class.
     */
    class Builder {
        private var id = 0L
        private lateinit var host: User
        private var name = "A Fun Party"
        private lateinit var playlist: Playlist
        private var nbRounds = 0
        private var withHint  = false
        private var isPrivate = false
        private var list = ArrayList<String>()

        /**
         * Set the [id] of the game.
         * @return the [Builder]
         */
        fun setID(id: Long): Builder {
            this.id = id
            return this
        }

        /**
         * Set the [host] for the game.
         * @return the [Builder]
         */
        fun setHost(host: User): Builder {
            this.host = host
            list.add(host.id)
            return this
        }

        /**
         * Set the [name] for the game.
         * @return the [Builder]
         */
        fun setName(name: String): Builder {
            this.name = name
            return this
        }

        /**
         * Set the [playlist] for the game.
         * @return the [Builder]
         */
        fun setPlaylist(playlist: Playlist): Builder {
            this.playlist = playlist
            return this
        }

        /**
         * Set the number of round ([nbRounds] for the game.
         * @return the [Builder]
         */
        fun setNbRounds(nbRounds: Int): Builder {
            this.nbRounds = nbRounds
            return this
        }

        /**
         * Set whether the game will have hints.
         *
         * @param withHint the boolean that says whether hints will be used or not.
         * @return the [Builder]
         */
        fun setWithHint(withHint: Boolean): Builder {
            this.withHint = withHint
            return this
        }

        /**
         * Set whether the game will be private.
         *
         * @param isPrivate the boolean that says whether the game will be private or not.
         * @return the [Builder]
         */
        fun setPrivacy(isPrivate: Boolean): Builder {
            this.isPrivate = isPrivate
            return this
        }

        /**
         * Add a [userId] to the game.
         * @return the [Builder]
         */
        fun addUserId(userId: String): Builder {
            list.add(userId)
            return this
        }

        /**
         * Add a collection of id of user to the game.
         * @return the [Builder]
         */
        fun addAllUserId(collection: Collection<String>): Builder {
            list.addAll(collection)
            return this
        }

        /**
         * Set the user id list with the given a collection of user ids.
         * @return the [Builder]
         */
        fun setUserIdList(collection: Collection<String>): Builder {
            list.clear()
            list.addAll(collection)
            return this
        }

        /**
        * Return the [Settings] of the game currently building.
        * @return the [Settings] of the game currently building.
        */
        fun getSettings(): Settings {
            return Settings(host.name, name, playlist.name, nbRounds, withHint, isPrivate)
        }

        /**
         * Build the [Game] with the previously given parameters.
         * @return the [Game] built with the previously given parameters.
         */
        fun build() : Game {
            return Game(id, name, host, playlist, nbRounds, withHint, isPrivate, list)
        }

    }

    data class Settings(
        val hostName: String,
        val name: String,
        val playlistName: String,
        val nbRounds: Int,
        val withHint: Boolean,
        val isPrivate: Boolean
    )

    companion object {

        /**
         * Compute the score of a player based on his [position].
         *
         * The computation goes as follows:
         *
         * The first player gets 100 points, the second one gets 85, the third one gets 70 and then 5 points are subtracted per place.
         * The minimum amount you can get when you guessed right is 10 points.
         *
         * @return the score of the player
         */
        fun computeScore(position: Int): Int {
            return when (position) {
                1 -> 100
                2 -> 85
                3 -> 70
                else -> {
                    min(70 - 5 * (position - 3), 10)
                }
            }
        }

    }
}