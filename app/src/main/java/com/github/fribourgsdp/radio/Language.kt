package com.github.fribourgsdp.radio

enum class Language(val id: Int, val code: String, val s: String) {
    ENGLISH(0,"en", "English"),
    FRENCH(1,"fr", "Français");

    override fun toString(): String {
        return s
    }

}