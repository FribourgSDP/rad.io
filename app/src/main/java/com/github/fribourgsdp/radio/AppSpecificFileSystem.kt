package com.github.fribourgsdp.radio

import android.content.Context
import java.io.File

class AppSpecificFileSystem (val context: Context) : FileSystem {
    override fun write(path: String, string: String) {
        val file = File(context.filesDir, path)
        file.writeText(string)
    }

    override fun read(path: String): String {
        val file = File(context.filesDir, path)
        return file.readText()
    }

    class AppSpecificFSGetter : FileSystem.FileSystemGetter {
        override fun getFileSystem(context: Context): FileSystem {
            return AppSpecificFileSystem(context)
        }
    }
}