package com.github.fribourgsdp.radio.activities.mockimplementations

import com.github.fribourgsdp.radio.backend.database.Database
import com.github.fribourgsdp.radio.activities.JoinGameActivity
import com.github.fribourgsdp.radio.backend.mockimplementations.BuggyDatabase
import com.github.fribourgsdp.radio.backend.mockimplementations.LocalDatabase

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
