package com.github.fribourgsdp.radio.mockimplementations

import com.github.fribourgsdp.radio.backend.database.Database
//import com.github.fribourgsdp.radio.JoinGameActivity
import com.github.fribourgsdp.radio.activities.JoinGameActivity

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
