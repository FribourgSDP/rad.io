package com.github.fribourgsdp.radio.persistence

import android.content.Context

interface FileSystem {
    /**
     * Writes or overwrites a string into a File at the specified path, creating the file
     * if it does not already exist
     *
     * @param path the path at which to write the file
     * @param string the string to write into the file
     */
    fun write(path: String, string: String)

    /**
     * reads a string fron a File at the specified path
     *
     * @param path the path at which to write the file
     * @return string the string read from the file
     *
     * @throws java.io.FileNotFoundException
     */
    fun read(path: String): String

    interface FileSystemGetter {
        fun getFileSystem(context: Context): FileSystem
    }
}