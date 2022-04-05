package com.github.fribourgsdp.radio.mockimplementations

import com.github.fribourgsdp.radio.Database
import com.github.fribourgsdp.radio.LobbyActivity
import com.github.fribourgsdp.radio.mockimplementations.BuggyDatabase
import com.github.fribourgsdp.radio.mockimplementations.LocalDatabase

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
