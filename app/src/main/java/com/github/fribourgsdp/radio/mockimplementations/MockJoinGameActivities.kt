package com.github.fribourgsdp.radio.mockimplementations

import com.github.fribourgsdp.radio.Database
import com.github.fribourgsdp.radio.JoinGameActivity
import com.github.fribourgsdp.radio.User

class WorkingJoinGameActivity : JoinGameActivity() {
    override fun initDatabase(): Database {
        return LocalDatabase()
    }

    override fun loadUser(): User {
        return User("The second best player")
    }
}

class BuggyJoinGameActivity : JoinGameActivity() {
    override fun initDatabase(): Database {
        return BuggyDatabase()
    }

    override fun loadUser(): User {
        return User("The buggy player")
    }
}
