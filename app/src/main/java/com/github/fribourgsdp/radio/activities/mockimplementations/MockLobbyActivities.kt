package com.github.fribourgsdp.radio.activities.mockimplementations

import com.github.fribourgsdp.radio.backend.database.Database
import com.github.fribourgsdp.radio.activities.LobbyActivity
import com.github.fribourgsdp.radio.backend.mockimplementations.BuggyDatabase
import com.github.fribourgsdp.radio.backend.mockimplementations.LocalDatabase

class WorkingLobbyActivity : LobbyActivity() {
    override fun initDatabase(): Database {
        return LocalDatabase()
    }
}

class BuggyLobbyActivity : LobbyActivity() {
    override fun initDatabase(): Database {
        return BuggyDatabase()
    }
}
