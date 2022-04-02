package com.github.fribourgsdp.radio

enum class Genre (private val genreName: String){
    NONE("No Specific Genre"),
    POP("POP"),
    HIPHOP("HIP-HOP"),
    ROCK("ROCK"),
    LATIN("LATIN"),
    KPOP("K-POP"),
    COUNTRY("COUNTRY");

    override fun toString(): String {
        return genreName
    }
}