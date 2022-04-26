package com.github.fribourgsdp.radio

enum class Language( val code: String, val s: String) {
    ENGLISH("en", "English"),
    FRENCH("fr", "Français");

    override fun toString(): String {
        return s
    }

}