package com.github.fribourgsdp.radio.mockimplementations

import com.github.fribourgsdp.radio.Database
import com.github.fribourgsdp.radio.JoinGameActivity

class WorkingJoinGameActivity : JoinGameActivity() {
    override fun initDatabase(): Database {
        return com.github.fribourgsdp.radio.LocalDatabase()
    }
}

class BuggyJoinGameActivity : JoinGameActivity() {
    override fun initDatabase(): Database {
        return com.github.fribourgsdp.radio.BuggyDatabase()
    }
}
