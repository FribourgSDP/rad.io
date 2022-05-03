package com.github.fribourgsdp.radio.mockimplementations

import android.content.Context
import android.os.Bundle
import com.github.fribourgsdp.radio.Database
import com.github.fribourgsdp.radio.JoinGameActivity
import com.github.fribourgsdp.radio.mockimplementations.BuggyDatabase
import com.github.fribourgsdp.radio.mockimplementations.LocalDatabase
import com.github.fribourgsdp.radio.User
import org.mockito.Mockito.*

class WorkingJoinGameActivity : JoinGameActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context : Context = mock(Context::class.java)
        User.setFSGetter(MockFileSystem.MockFSGetter)
        User("The second best player").save(context)
    }

    override fun initDatabase(): Database {
        return testDatabase
    }

    companion object {
        val testDatabase = LocalDatabase()
    }
}

class BuggyJoinGameActivity : JoinGameActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val context : Context = mock(Context::class.java)
        User.setFSGetter(MockFileSystem.MockFSGetter)
        User("The buggy player").save(context)
    }

    override fun initDatabase(): Database {
        return BuggyDatabase()
    }
}
