package com.github.fribourgsdp.radio.mockimplementations

import com.github.fribourgsdp.radio.Database
import com.github.fribourgsdp.radio.LobbyActivity

class WorkingJoinGameActivity : LobbyActivity() {
    override fun initDatabase(): Database {
        return LocalDatabase()
    }
}

class BuggyJoinGameActivity : LobbyActivity() {
    override fun initDatabase(): Database {
        return BuggyDatabase()
    }
}
