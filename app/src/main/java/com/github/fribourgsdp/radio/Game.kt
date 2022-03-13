package com.github.fribourgsdp.radio

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.HashSet

class Game private constructor(val name: String, val host: User, val playlist: Playlist, val nbRounds: Int,
                               val withHint: Boolean, val isPrivate: Boolean, private val listUser: List<User>) {

    private val scoreMap = HashMap(listUser.associateWith { 0 })
    private var usersToPlay = listUser.size

    var currentRound = 1
        private set

    private val songsNotDone = HashSet(playlist.getSongs())

    fun getScore(user: User): Int? {
        return scoreMap[user]
    }

    fun addPoints(user: User, points: Int) {
        val oldValue = scoreMap[user]
        if (oldValue != null) {
            scoreMap[user] = oldValue + points
        } else {
            throw IllegalArgumentException("Illegal argument: user '${user.name}' doesn't exist.")
        }

    }

    fun getUserToPlay(): User {
        val user = listUser[0]
        Collections.rotate(listUser, 1)
        if (usersToPlay == 0) {
            ++currentRound
            usersToPlay = listUser.size
        }
        --usersToPlay

        return user
    }

    fun getChoices(nb: Int): Set<Song> {
        // Check if all the songs have been done and restart if so
        if (songsNotDone.isEmpty()) {
            songsNotDone.addAll(playlist.getSongs())
        }

        val chosen = HashSet<Song>()

        for (i in 1..nb) {
            val random = songsNotDone.random()
            chosen.add(random)
            songsNotDone.remove(random)
        }

        return chosen
    }

    fun isDone(): Boolean {
        return nbRounds <= currentRound && usersToPlay <= 0
    }

    class Builder {
        private lateinit var host: User
        private var name = "A Fun Party"
        private lateinit var playlist: Playlist
        private var nbRounds = 0
        private var withHint  = false
        private var isPrivate = false
        private var list = ArrayList<User>()

        fun setHost(host: User): Builder {
            this.host = host
            list.add(host)
            return this
        }

        fun setName(name: String): Builder {
            this.name = name
            return this
        }

        fun setPlaylist(playlist: Playlist): Builder {
            this.playlist = playlist
            return this
        }

        fun setNbRounds(nbRounds: Int): Builder {
            this.nbRounds = nbRounds
            return this
        }

        fun setWithHint(withHint: Boolean): Builder {
            this.withHint = withHint
            return this
        }

        fun setPrivacy(isPrivate: Boolean): Builder {
            this.isPrivate = isPrivate
            return this
        }

        fun addUser(user: User): Builder {
            list.add(user)
            return this
        }


        fun build() : Game {
            return Game(name, host, playlist, nbRounds, withHint, isPrivate, list)
        }

    }
}