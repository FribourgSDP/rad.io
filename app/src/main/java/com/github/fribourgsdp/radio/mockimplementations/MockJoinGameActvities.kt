package com.github.fribourgsdp.radio.mockimplementations

import com.github.fribourgsdp.radio.Database
import com.github.fribourgsdp.radio.JoinGameActivity
import com.github.fribourgsdp.radio.LobbyActivity

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
