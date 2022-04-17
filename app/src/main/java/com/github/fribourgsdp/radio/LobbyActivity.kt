package com.github.fribourgsdp.radio

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val GAME_KEY = "com.github.fribourgsdp.radio.GAME"
const val MAP_ID_NAME_KEY = "com.github.fribourgsdp.radio.MAP_ID_NAME"
const val PERMISSION_REQ_ID_RECORD_AUDIO = 22
const val NO_MIC_PERMISSIONS_DRAWABLE = R.drawable.ic_action_name
const val MIC_PERMISSIONS_ENABLED_DRAWABLE = R.drawable.ic_unmute

open class LobbyActivity : AppCompatActivity(){

    private val db = this.initDatabase()

    private lateinit var user : User
    private var hostName : String? = null
    private var gameName : String? = null
    private var playlist : Playlist? = null
    private var playlistName : String? = null
    private var nbRounds: Int = 0
    private var withHint: Boolean = false
    private var isPrivate: Boolean = false
    private var isHost: Boolean = false
    private var hasVoiceIdPermissions : Boolean = false
    private var gameLobbyId: Long = -1L

    private lateinit var uuidTextView     : TextView
    private lateinit var hostNameTextView : TextView
    private lateinit var gameNameTextView : TextView
    private lateinit var playlistTextView : TextView
    private lateinit var nbRoundsTextView : TextView
    private lateinit var withHintTextView : TextView
    private lateinit var privateTextView  : TextView

    private lateinit var launchGameButton: Button
    private lateinit var askForPermissionsButton: Button

    private var layoutManager:RecyclerView.LayoutManager? = null
    private var layoutAdapter:RecyclerLobbyAdapter? = null

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
            gameLobbyId = uid
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
        if (gameLobbyId >= 0) {
            uuidTextView.text = getString(R.string.uid_text_format, gameLobbyId)
            linkToDatabase(gameLobbyId)
        } else {
            uuidTextView.text = getString(R.string.uid_error_join)
        }
    }

    open fun initDatabase(): Database {
        return FirestoreDatabase()
    }

    private fun initVariables() {
        user = User.load(this)

        playlist = intent.getStringExtra(GAME_PLAYLIST_KEY)?.let {
            Json.decodeFromString(it) as Playlist
        }

        isHost = intent.getBooleanExtra(GAME_IS_HOST_KEY, false)

        // if user is not the host, get the name from the intent
        hostName = if (isHost) user.name else intent.getStringExtra(GAME_HOST_NAME_KEY)

        playlistName = playlist?.name
                // if playlist is null, get the name from the intent
            ?: intent.getStringExtra(GAME_PLAYLIST_NAME_KEY)

        gameName        = intent.getStringExtra(GAME_NAME_KEY)
        nbRounds        = intent.getIntExtra(GAME_NB_ROUNDS_KEY, getString(R.string.default_game_nb_rounds).toInt())
        withHint        = intent.getBooleanExtra(GAME_HINT_KEY, false)
        isPrivate       = intent.getBooleanExtra(GAME_PRIVACY_KEY, false)

        hasVoiceIdPermissions = (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
        gameLobbyId = intent.getLongExtra(GAME_UID_KEY, -1L)
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
        layoutManager = LinearLayoutManager(this)
        val recyclerView = findViewById<RecyclerView>(R.id.lobbyRecyclerView)
        recyclerView.layoutManager = layoutManager
        layoutAdapter = RecyclerLobbyAdapter()
        recyclerView.adapter = layoutAdapter
        if (isHost) {
            val hostMicPermissionsImg = if (hasVoiceIdPermissions) MIC_PERMISSIONS_ENABLED_DRAWABLE else NO_MIC_PERMISSIONS_DRAWABLE
            layoutAdapter?.setContent(arrayOf(user.name), intArrayOf(hostMicPermissionsImg))
            layoutAdapter?.notifyDataSetChanged()
        }
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
            if (gameName != null && playlist != null) {
                gameBuilder.setHost(user)
                    .setID(uid)
                    .setName(gameName!!)
                    .setPlaylist(playlist!!)
                    .setNbRounds(nbRounds)
                    .setWithHint(withHint)
                    .setPrivacy(isPrivate)


                openLobby(uid)
            } else {
                uuidTextView.text = getString(R.string.uid_error)
            }
        } else if (!isHost && uid >= 0) {
            listenToUpdates(uid)
        }

    }


    private fun openLobby(uid: Long){
        db.openLobby(uid, gameBuilder.getSettings()).addOnSuccessListener {
                    db.addUserToLobby(uid, user, (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)).addOnSuccessListener {
                        listenToUpdates(uid)
                    }.addOnFailureListener {
                        uuidTextView.text = getString(R.string.uid_error)
                    }
                }.addOnFailureListener {
                    uuidTextView.text = getString(R.string.uid_error)
                }
    }

    private fun listenToUpdates(uid: Long) {
        db.listenToLobbyUpdate(uid) { snapshot, e ->
            if (e != null) {
                uuidTextView.text = getString(R.string.uid_error)
            }

            if (snapshot != null && snapshot.exists()) {
                val newList = snapshot.get("players")!! as ArrayList<HashMap<String, String>>

                val isGameLaunched = snapshot.getBoolean("launched")

                val mapNameToPermissions = snapshot.get("permissions")!! as HashMap<String, Boolean>
                val atLeastOnePermissionMissing = mapNameToPermissions.containsValue(false)
                launchGameButton.isEnabled = !atLeastOnePermissionMissing

                updatePlayersList(mapNameToPermissions, newList)


                if (!isHost && isGameLaunched != null && isGameLaunched) {
                    goToGameActivity(false, gameID = uid)
                }

            } else {
                uuidTextView.text = getString(R.string.uid_error)
            }

        }
    }

    private fun updatePlayersList(nameToPermissions: Map<String, Boolean>, playersList: List<Map<String, String>>) {
        val users = playersList.map { u -> u["name"]!! }
        val micPermissions = arrayListOf<Int>()
        for (user in users) {
            if (nameToPermissions[user]!!) {
                micPermissions.add(MIC_PERMISSIONS_ENABLED_DRAWABLE)
            }
            else {
                micPermissions.add(NO_MIC_PERMISSIONS_DRAWABLE)
            }
        }
        layoutAdapter?.setContent(users.toTypedArray(), micPermissions.toIntArray())
        layoutAdapter?.notifyDataSetChanged()
        gameBuilder.setUserIdList(playersList.map { u -> u["id"]!! })
        mapIdToName = playersList.associate {
            it["id"]!! to it["name"]!!
        } as HashMap<String, String>
    }


    open protected fun goToGameActivity(isHost: Boolean, game: Game? = null, gameID: Long) {
        val intent: Intent = Intent(this, GameActivity::class.java).apply {
            putExtra(GAME_IS_HOST_KEY, isHost)
            putExtra(MAP_ID_NAME_KEY, mapIdToName)
            putExtra(GAME_UID_KEY, gameID)
        }

        if (isHost && game != null) {
            intent.putExtra(GAME_KEY, Json.encodeToString(game))
        }

        startActivity(intent)
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
                    db.modifyUserMicPermissions(gameLobbyId, user, true)
                }
            }
        }
    }
}