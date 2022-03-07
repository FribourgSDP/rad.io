import java.util.*

class Song (songName: String, artistName: String, songLyrics: String) {

    var name: String = reformat_song_name(songName)
        get() {return field}
        set(value) {field = reformat_song_name(value)}

    var artist: String = artistName
    var lyrics: String = songLyrics

    constructor (song_name: String): this(song_name, "", "")
    constructor(song_name:String, artist_name: String): this(song_name, artist_name, "")

    private fun reformat_song_name(unformattedSongName: String): String {
        val noSpacesRegex = Regex(" +")
        val songNameWords = unformattedSongName.trim().split(noSpacesRegex)
        val result: MutableList<String> = mutableListOf()
        for (elem in songNameWords) {
            var word: String = elem
            if (word[0].isLetter() && word[0].isLowerCase()){
                word = word[0].toUpperCase() + word.substring(1)
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
