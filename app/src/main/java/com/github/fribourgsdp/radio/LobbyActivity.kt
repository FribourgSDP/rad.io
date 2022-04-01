package com.github.fribourgsdp.radio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val GAME_KEY = "com.github.fribourgsdp.radio.GAME"

open class LobbyActivity : AppCompatActivity() {
    private val json = Json {
        allowStructuredMapKeys = true
    }

    private val db = this.initDatabase()

    private var host : User? = null
    private var gameName : String? = null
    private var playlist : Playlist? = null
    private var nbRounds: Int = 0
    private var withHint: Boolean = false
    private var isPrivate: Boolean = false
    private var isHost: Boolean = false

    private lateinit var uuidTextView     : TextView
    private lateinit var hostNameTextView : TextView
    private lateinit var gameNameTextView : TextView
    private lateinit var playlistTextView : TextView
    private lateinit var nbRoundsTextView : TextView
    private lateinit var withHintTextView : TextView
    private lateinit var privateTextView  : TextView

    private lateinit var launchGameButton: Button

    private lateinit var playersListView : ListView
    private lateinit var namesAdapter : ArrayAdapter<String>

    private val gameBuilder = Game.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)

        initVariables()
        initTextViews()
        updateTextViews()

        if (isHost) {
            initLobbyAsHost()
        } else {
            initLobbyAsPlayer()
        }

    }

    private fun initLobbyAsHost() {
        db.getLobbyId().addOnSuccessListener { uid ->
            uuidTextView.text = getString(R.string.uid_text_format, uid)
            linkToDatabase(uid)

            // Setup the launch button for hosts
            launchGameButton.setOnClickListener {
                val game = gameBuilder.build()

                // Try to open the game on the database
                db.openGame(uid).addOnSuccessListener {

                    // Try to put the game metadata on the database
                    db.openGameMetadata(uid, game.getAllPlayers()).addOnSuccessListener {

                        // If the game is created, try to launch it
                        db.launchGame(uid).addOnSuccessListener {

                            // When launched correctly, go to the game activity
                            goToGameActivity(true, game)

                        }.addOnFailureListener {
                            uuidTextView.text = getString(R.string.launch_game_error)
                        }

                    }.addOnFailureListener {
                    uuidTextView.text = getString(R.string.open_game_error)
                }

                }.addOnFailureListener {
                    uuidTextView.text = getString(R.string.open_game_error)
                }
            }

        }.addOnFailureListener {
            uuidTextView.text = getString(R.string.uid_error)
        }
    }

    private fun initLobbyAsPlayer() {
        val uid = intent.getLongExtra(GAME_UID_KEY, -1)
        if (uid >= 0) {
            uuidTextView.text = getString(R.string.uid_text_format, uid)
            linkToDatabase(uid)
        } else {
            uuidTextView.text = getString(R.string.uid_error_join)
        }
    }

    open fun initDatabase(): Database {
        return FirestoreDatabase()
    }

    private fun initVariables() {
        val hostIntent = intent.getStringExtra(GAME_HOST_KEY)
        val playlistIntent = intent.getStringExtra(GAME_PLAYLIST_KEY)

        if (hostIntent != null && playlistIntent != null) {
            host            = json.decodeFromString(hostIntent) as User
            playlist        = json.decodeFromString(playlistIntent) as Playlist
        }

        gameName        = intent.getStringExtra(GAME_NAME_KEY)
        nbRounds        = intent.getIntExtra(GAME_NB_ROUNDS_KEY, getString(R.string.default_game_nb_rounds).toInt())
        withHint        = intent.getBooleanExtra(GAME_HINT_KEY, false)
        isPrivate       = intent.getBooleanExtra(GAME_PRIVACY_KEY, false)
        isHost          = intent.getBooleanExtra(GAME_IS_HOST_KEY, false)
    }

    private fun initTextViews() {
        uuidTextView     = findViewById(R.id.uuidText)
        hostNameTextView = findViewById(R.id.hostNameText)
        gameNameTextView = findViewById(R.id.gameNameText)
        playlistTextView = findViewById(R.id.playlistText)
        nbRoundsTextView = findViewById(R.id.nbRoundsText)
        withHintTextView = findViewById(R.id.withHintText)
        privateTextView  = findViewById(R.id.privateText)
        privateTextView  = findViewById(R.id.privateText)
        playersListView = findViewById(R.id.playersListView)
        namesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        playersListView.adapter = namesAdapter

        launchGameButton = findViewById(R.id.launchGameButton)

        if (!isHost) {
            launchGameButton.visibility = View.INVISIBLE
        }
    }

    private fun updateTextViews() {
        hostNameTextView.text = getString(R.string.host_name_format, host?.name ?: "")
        gameNameTextView.text = getString(R.string.game_name_format, gameName)
        playlistTextView.text = getString(R.string.playlist_format, playlist?.name ?: "")
        nbRoundsTextView.text = getString(R.string.number_of_rounds_format, nbRounds)
        withHintTextView.text = getString(R.string.hints_enabled_format, withHint)
        privateTextView.text  = getString(R.string.private_format, isPrivate)
    }

    private fun linkToDatabase(uid: Long) {
        if (isHost && uid >= 0) {
            if (gameName != null && host != null && playlist != null) {
                gameBuilder.setHost(host!!)
                    .setID(uid)
                    .setName(gameName!!)
                    .setPlaylist(playlist!!)
                    .setNbRounds(nbRounds)
                    .setWithHint(withHint)
                    .setPrivacy(isPrivate)

                db.openLobby(uid, gameBuilder.getSettings()).addOnSuccessListener {
                    listenToUpdates(uid)
                }.addOnFailureListener {
                    uuidTextView.text = getString(R.string.uid_error)
                }
            } else {
                uuidTextView.text = getString(R.string.uid_error)
            }
        } else if (!isHost && uid >= 0) {
            listenToUpdates(uid)
        }

    }

    private fun listenToUpdates(uid: Long) {
        db.listenToLobbyUpdate(uid) { snapshot, e ->
            if (e != null) {
                uuidTextView.text = getString(R.string.uid_error)
            }

            if (snapshot != null && snapshot.exists()) {
                val newList = snapshot.get("players")!! as ArrayList<String>
                updatePlayersList(newList)

                val isGameLaunched = snapshot.getBoolean("launched")

                if (!isHost && isGameLaunched != null && isGameLaunched) {
                    goToGameActivity(false, gameID = uid)
                }

            } else {
                uuidTextView.text = getString(R.string.uid_error)
            }

        }
    }

    private fun updatePlayersList(playersList: List<String>) {
        namesAdapter.clear()
        namesAdapter.addAll(playersList)
        namesAdapter.notifyDataSetChanged()
        gameBuilder.setUserList(playersList.map { name -> User(name) })
    }

    private fun goToGameActivity(isHost: Boolean, game: Game? = null, gameID: Long? = null) {
        val intent: Intent = Intent(this, GameActivity::class.java).apply {
            putExtra(GAME_IS_HOST_KEY, isHost)
        }

        if (isHost && game != null) {
            intent.putExtra(GAME_KEY, json.encodeToString(game))
        } else if (gameID != null) {
            intent.putExtra(GAME_UID_KEY, gameID)
        }

        startActivity(intent)
    }
}