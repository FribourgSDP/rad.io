package com.github.fribourgsdp.radio.mockimplementations

import android.content.Intent
import android.os.Bundle
import com.github.fribourgsdp.radio.*
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.game.Game
import com.github.fribourgsdp.radio.game.prep.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class WorkingLobbyActivity : LobbyActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        User.setFSGetter(MockFileSystem.MockFSGetter)
        val testUser = User("the best player")
        testUser.id = "123456789"
        testUser.save(this)
        super.onCreate(savedInstanceState)
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

class LyricsGettingWorkingLobbyActivity : LobbyActivity() {
    override fun initDatabase(): Database {
        return LocalDatabase()
    }
    lateinit var host : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        host = User("Test User")
        val name = "Test Game"
        val playlistTest = Playlist("Stoner Playlist")
        playlistTest.addSong(Song("Momentum", "Truckfighters"))
        host.addPlaylist(playlistTest)

        goToGameActivity(true, Game.Builder()
            .setHost(host)
            .setName(name)
            .setPlaylist(playlistTest)
            .setWithHint(false)
            .setNbRounds(1)
            .setPrivacy(true).build(),
            44L)
    }

    override fun goToGameActivity(isHost: Boolean, game: Game?, gameID: Long) {

        if (isHost && game != null) {
            intent.putExtra(GAME_KEY, Json.encodeToString(game))
            loadLyrics(game.playlist, MockLyricsGetter, host)
        }
    }
}
