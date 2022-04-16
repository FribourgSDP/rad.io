package com.github.fribourgsdp.radio

interface FileSystem {
    fun write(path: String, string: String)

    fun read(path: String): String
}