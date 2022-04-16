package com.github.fribourgsdp.radio

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val GAME_KEY = "com.github.fribourgsdp.radio.GAME"
const val MAP_ID_NAME_KEY = "com.github.fribourgsdp.radio.MAP_ID_NAME"
const val PERMISSION_REQ_ID_RECORD_AUDIO = 22

open class LobbyActivity : AppCompatActivity() {

    private val db = this.initDatabase()

    private var host : User? = null
    private var hostName : String? = null
    private var gameName : String? = null
    private var playlist : Playlist? = null
    private var playlistName : String? = null
    private var nbRounds: Int = 0
    private var withHint: Boolean = false
    private var isPrivate: Boolean = false
    private var isHost: Boolean = false
    private var hasVoiceIdPermissions : Boolean = false

    private lateinit var uuidTextView     : TextView
    private lateinit var hostNameTextView : TextView
    private lateinit var gameNameTextView : TextView
    private lateinit var playlistTextView : TextView
    private lateinit var nbRoundsTextView : TextView
    private lateinit var withHintTextView : TextView
    private lateinit var privateTextView  : TextView

    private lateinit var launchGameButton: Button
    private lateinit var askForPermissionsButton: Button

    private lateinit var playersListView : ListView
    private lateinit var namesAdapter : ArrayAdapter<String>

    protected lateinit var mapIdToName: HashMap<String, String>

    private val gameBuilder = Game.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)

        initVariables()
        initTextViews()
        initButtons()
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
                    db.openGameMetadata(uid, game.getAllPlayersId()).addOnSuccessListener {

                        // If the game is created, try to launch it
                        db.launchGame(uid).addOnSuccessListener {

                            // When launched correctly, go to the game activity
                            goToGameActivity(true, game, uid)

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
        host = intent.getStringExtra(GAME_HOST_KEY)?.let {
            Json.decodeFromString(it) as User
        }

        playlist = intent.getStringExtra(GAME_PLAYLIST_KEY)?.let {
            Json.decodeFromString(it) as Playlist
        }

        hostName = host?.name
                // if host is null, get the name from the intent
                ?: intent.getStringExtra(GAME_HOST_NAME_KEY)

        playlistName = playlist?.name
                // if playlist is null, get the name from the intent
            ?: intent.getStringExtra(GAME_PLAYLIST_NAME_KEY)

        gameName        = intent.getStringExtra(GAME_NAME_KEY)
        nbRounds        = intent.getIntExtra(GAME_NB_ROUNDS_KEY, getString(R.string.default_game_nb_rounds).toInt())
        withHint        = intent.getBooleanExtra(GAME_HINT_KEY, false)
        isPrivate       = intent.getBooleanExtra(GAME_PRIVACY_KEY, false)
        isHost          = intent.getBooleanExtra(GAME_IS_HOST_KEY, false)

        hasVoiceIdPermissions = (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
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
    }

    private fun initButtons() {
        launchGameButton = findViewById(R.id.launchGameButton)
        askForPermissionsButton = findViewById(R.id.micPermissionsButton)
        if (!isHost) {
            launchGameButton.visibility = View.INVISIBLE
        }
        if (hasVoiceIdPermissions) {
            askForPermissionsButton.visibility = View.INVISIBLE
        }
        else {
            launchGameButton.isEnabled = false
            askForPermissionsButton.setOnClickListener {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSION_REQ_ID_RECORD_AUDIO)
            }
        }
    }

    private fun updateTextViews() {
        hostNameTextView.text = getString(R.string.host_name_format, hostName ?: "")
        gameNameTextView.text = getString(R.string.game_name_format, gameName)
        playlistTextView.text = getString(R.string.playlist_format, playlistName ?: "")
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
                    db.addUserToLobby(uid, host!!).addOnSuccessListener {
                        listenToUpdates(uid)
                    }.addOnFailureListener {
                        uuidTextView.text = getString(R.string.uid_error)
                    }
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
                val newList = snapshot.get("players")!! as ArrayList<HashMap<String, String>>
                updatePlayersList(newList)

                val isGameLaunched = snapshot.getBoolean("launched")

                if (!isHost && isGameLaunched != null && isGameLaunched && hasVoiceIdPermissions) {
                    goToGameActivity(false, gameID = uid)
                }

            } else {
                uuidTextView.text = getString(R.string.uid_error)
            }

        }
    }

    private fun updatePlayersList(playersList: List<Map<String, String>>) {
        namesAdapter.clear()
        namesAdapter.addAll(playersList.map {u -> u["name"] })
        namesAdapter.notifyDataSetChanged()
        gameBuilder.setUserIdList(playersList.map { u -> u["id"]!! })
        mapIdToName = playersList.associate {
            it["id"]!! to it["name"]!!
        } as HashMap<String, String>
    }

    protected open fun goToGameActivity(isHost: Boolean, game: Game? = null, gameID: Long) {
        val intent: Intent = Intent(this, GameActivity::class.java).apply {
            putExtra(GAME_IS_HOST_KEY, isHost)
            putExtra(MAP_ID_NAME_KEY, mapIdToName)
            putExtra(GAME_UID_KEY, gameID)
        }

        if (isHost && game != null) {
            intent.putExtra(GAME_KEY, Json.encodeToString(game))
            loadLyrics(game.playlist, MusixmatchLyricsGetter, host!!)
        }

        startActivity(intent)
    }
    protected fun loadLyrics(playlist : Playlist, lyricsGetter: LyricsGetter, host : User){
        if (host.getPlaylists().contains(playlist)){
            for (song in playlist.getSongs()){
                if(song.lyrics == "") {
                    lyricsGetter.getLyrics(song.name, song.artist).thenAccept { f ->
                        val songWithLyrics = Song(song.name, song.artist, f)
                        host.updateSongInPlaylist(playlist, songWithLyrics)
                        host.save(applicationContext)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Test if received callback indeed comes from a microphone permission request
        if (requestCode == PERMISSION_REQ_ID_RECORD_AUDIO) {
            for (i in permissions.indices) {
                var permission: String = permissions[i]
                var granted: Int = grantResults[i]
                if (permission == Manifest.permission.RECORD_AUDIO && granted == PackageManager.PERMISSION_GRANTED) {
                    hasVoiceIdPermissions = true
                    launchGameButton.isEnabled = true
                    askForPermissionsButton.visibility = View.INVISIBLE
                }
            }
        }
    }
}