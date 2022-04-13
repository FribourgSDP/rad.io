package com.github.fribourgsdp.radio.mockimplementations

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.github.fribourgsdp.radio.*
import com.github.fribourgsdp.radio.mockimplementations.BuggyDatabase
import com.github.fribourgsdp.radio.mockimplementations.LocalDatabase
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WorkingLobbyActivity : LobbyActivity() {
    override fun initDatabase(): Database {
        return LocalDatabase()

    }

    override fun goToGameActivity(isHost: Boolean, game: Game?, gameID: Long) {
        val intent: Intent = Intent(this, MockGameActivity::class.java).apply {
            putExtra(GAME_IS_HOST_KEY, isHost)
            putExtra(MAP_ID_NAME_KEY, mapIdToName)
            putExtra(GAME_UID_KEY, gameID)
        }

        if (isHost && game != null) {
            intent.putExtra(GAME_KEY, Json.encodeToString(game))
        }

        startActivity(intent)
    }

    override fun loadUser(): User {
        return User("the best player")
    }
}

class BuggyLobbyActivity : LobbyActivity() {
    override fun initDatabase(): Database {
        return BuggyDatabase()
    }

    override fun goToGameActivity(isHost: Boolean, game: Game?, gameID: Long) {
        val intent: Intent = Intent(this, MockGameActivity::class.java).apply {
            putExtra(GAME_IS_HOST_KEY, isHost)
            putExtra(MAP_ID_NAME_KEY, mapIdToName)
            putExtra(GAME_UID_KEY, gameID)
        }

        if (isHost && game != null) {
            intent.putExtra(GAME_KEY, Json.encodeToString(game))
        }

        startActivity(intent)
    }

    override fun loadUser(): User {
        return User("the buggy player")
    }
}
