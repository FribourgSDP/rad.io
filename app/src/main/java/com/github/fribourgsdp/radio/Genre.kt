package com.github.fribourgsdp.radio

enum class Genre (private val genreName: String){
    NONE("No Specific Genre"),
    POP("Pop"),
    HIPHOP("Hip-hop"),
    ROCK("Rock"),
    LATIN("Latin"),
    KPOP("K-Pop"),
    METAL("Metal"),
    FRENCH("Chanson Française"),
    COUNTRY("Country");

    override fun toString(): String {
        return genreName
    }
}