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
    JAZZ("Jazz"),
    SOUL("Soul"),
    FOLK("Folk"),
    PUNK("Punk"),
    RNB("RnB"),
    BLUES("Blues"),
    FUNK("Funk"),
    REGGAE("Reggae"),
    ELECTRO("Electro"),
    CLASSICAL("Classical"),
    VIDEO_GAMES("Video Games Music"),
    MOVIE("Movie Music"),
    COUNTRY("Country");

    /*override fun toString(): String {
        return genreName
    }*/
}