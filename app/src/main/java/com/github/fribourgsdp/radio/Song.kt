package com.github.fribourgsdp.radio

import java.util.*
import kotlinx.serialization.Serializable
import java.util.concurrent.CompletableFuture

@Serializable
/**
 * A data class for songs with immutable name and artist but mutable lyrics.
 * Serializable: supports the use of Json.encodeToString(<song>) and Json.decodeFromString(<string>)
 * so that it can be saved and loaded to files or passed through intents as strings
 * (NOT as serializable Objects in intents, those implement Java.io.serializable)
 *
 * @property rawName name of the song which will be reformatted and accessible with <song>.name
 * @property rawArtist name of the artist which will be reformatted and accessible with <song>.artist
 * @property lyrics mutable lyrics of the song
 * @property name the reformatted name of the song
 * @property artist the reformatted name of the artist of the song
 * @constructor creates a Song with the given name, artist name and possibly blank lyrics
 */
class Song (private val rawName: String, private val rawArtist: String, var lyrics: String) {
    val name: String = reformatName(rawName)
    val artist: String = reformatName(rawArtist)
    var id: String = ""

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