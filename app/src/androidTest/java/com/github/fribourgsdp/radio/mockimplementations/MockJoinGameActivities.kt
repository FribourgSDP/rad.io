package com.github.fribourgsdp.radio.mockimplementations

import com.github.fribourgsdp.radio.Database
import com.github.fribourgsdp.radio.JoinGameActivity
import com.github.fribourgsdp.radio.mockimplementations.BuggyDatabase
import com.github.fribourgsdp.radio.mockimplementations.LocalDatabase

class WorkingJoinGameActivity : JoinGameActivity() {
    override fun initDatabase(): Database {
        return LocalDatabase()
    }
}

class BuggyJoinGameActivity : JoinGameActivity() {
    override fun initDatabase(): Database {
        return BuggyDatabase()
    }
}
