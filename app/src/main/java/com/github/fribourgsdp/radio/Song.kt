package com.github.fribourgsdp.radio

import java.util.*
import java.util.concurrent.CompletableFuture

class Song (songName: String, artistName: String, var lyrics: String) {

    var name: String = reformatName(songName)
        set(value) {field = reformatName(value)}
    var artist: String = reformatName(artistName)
        set(value) {field = reformatName(value)}

    constructor (songName: String): this(songName, "", "")
    constructor(songName:String, artistName: String): this(songName, artistName, "")

    private fun reformatName(unformattedName: String): String {
        val noSpacesRegex = Regex(" +")
        val words = unformattedName.trim().split(noSpacesRegex)
        if (words.size == 1 && words[0].isEmpty()){
            return ""
        }
        val result: MutableList<String> = mutableListOf()
        for (elem in words) {
            var word: String = elem
            if (word[0].isLetter() && word[0].isLowerCase()){
                word = word[0].uppercaseChar() + word.substring(1)
            }
            result.add(word)
        }
        return result.joinToString(" ")
    }

    override fun equals(other: Any?): Boolean {
        return (other is Song)
                && other.name == (this.name) //In Kotlin, == is equivalent to .equals for String, whereas === checks for referential equality.
                && other.artist == (this.artist)
    }

    override fun hashCode(): Int {
        return Objects.hash(name, artist)
    }
}