package com.github.fribourgsdp.radio.mockimplementations

import com.github.fribourgsdp.radio.Database
import com.github.fribourgsdp.radio.LobbyActivity

class WorkingLobbyActivity : LobbyActivity() {
    override fun initDatabase(): Database {
        return com.github.fribourgsdp.radio.LocalDatabase()
    }
}

class BuggyLobbyActivity : LobbyActivity() {
    override fun initDatabase(): Database {
        return com.github.fribourgsdp.radio.BuggyDatabase()
    }
}
