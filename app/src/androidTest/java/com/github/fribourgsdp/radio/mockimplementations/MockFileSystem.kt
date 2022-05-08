package com.github.fribourgsdp.radio.mockimplementations

import android.content.Context
import com.github.fribourgsdp.radio.persistence.FileSystem

var data: HashMap<String, String> = HashMap()

class MockFileSystem : FileSystem {

    override fun write(path: String, string: String) {
        data[path] = string
    }

    override fun read(path: String): String {
        return data[path]!!
    }

    companion object MockFSGetter : FileSystem.FileSystemGetter {
        override fun getFileSystem(context: Context): FileSystem {
            return MockFileSystem()
        }

        fun wipeData() {
            data = HashMap()
        }
    }
}