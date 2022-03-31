package com.github.fribourgsdp.radio

import java.util.*
import kotlinx.serialization.Serializable
import java.util.concurrent.CompletableFuture

@Serializable
class Song (private val rawName: String, private val rawArtist: String, var lyrics: String): java.io.Serializable{

    val name: String = reformatName(rawName)
    val artist: String = reformatName(rawArtist)

    constructor(name: String, artist: String): this(name, artist,"")
    constructor(name: String, artist: String, lyrics: CompletableFuture<String>): this(name, artist, lyrics.get())


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