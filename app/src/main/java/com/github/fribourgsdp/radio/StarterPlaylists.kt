package com.github.fribourgsdp.radio

import com.github.fribourgsdp.radio.Genre.*

object StarterPlaylists {
    val french80 = Playlist("Chanson française 80\'s", FRENCH)
    init {
        french80.addSongs(setOf(
            "L\'aventurier" to "Indochine",
            "Je te donne" to "Jean-Jacques Goldman, Michael Jones",
            "Elle a les yeux revolver" to "Marc Lavoine"
        ).map { tuple -> Song(tuple.first, tuple.second) }.toSet())
    }
}