package com.github.fribourgsdp.radio.mockimplementations

import com.github.fribourgsdp.radio.backend.Database
import com.github.fribourgsdp.radio.activities.LobbyActivity

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
