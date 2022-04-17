package com.github.fribourgsdp.radio.mockimplementations

import android.content.Context
import com.github.fribourgsdp.radio.FileSystem

class MockFileSystem : FileSystem {
    private var data: String = ""

    override fun write(path: String, string: String) {
        data = string
    }

    override fun read(path: String): String {
        return data
    }

    companion object MockFSGetter : FileSystem.FileSystemGetter {
        override fun getFileSystem(context: Context): FileSystem {
            return MockFileSystem()
        }
    }
}