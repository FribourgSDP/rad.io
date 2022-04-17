package com.github.fribourgsdp.radio.mockimplementations

import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat.startActivity
import com.github.fribourgsdp.radio.*
import com.github.fribourgsdp.radio.mockimplementations.BuggyDatabase
import com.github.fribourgsdp.radio.mockimplementations.LocalDatabase
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WorkingLobbyActivity : LobbyActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        User.setFSGetter(MockFileSystem.MockFSGetter)
        val testUser = User("the best player")
        testUser.id = "123456789"
        super.onCreate(savedInstanceState)
        testUser.save(this)
    }

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
}

class BuggyLobbyActivity : LobbyActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        User.setFSGetter(MockFileSystem.MockFSGetter)
        val testUser = User("the buggy player")
        testUser.id = "9999999"
        testUser.save(this)
        super.onCreate(savedInstanceState)
    }

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
}
