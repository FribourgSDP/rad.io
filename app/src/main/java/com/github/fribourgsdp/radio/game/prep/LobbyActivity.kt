package com.github.fribourgsdp.radio.game.prep

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.fribourgsdp.radio.MainActivity
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.config.MyAppCompatActivity
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.database.FirestoreDatabase
import com.github.fribourgsdp.radio.database.LAUNCHED_KEY
import com.github.fribourgsdp.radio.database.VALIDITY_KEY
import com.github.fribourgsdp.radio.game.Game
import com.github.fribourgsdp.radio.game.GameActivity
import com.github.fribourgsdp.radio.game.view.QuitGameOrLobbyDialog
import com.github.fribourgsdp.radio.util.getAndCast
import com.github.fribourgsdp.radio.util.getPermissions
import com.github.fribourgsdp.radio.util.getPlayers
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val GAME_KEY = "com.github.fribourgsdp.radio.GAME"
const val MAP_ID_NAME_KEY = "com.github.fribourgsdp.radio.MAP_ID_NAME"
const val PERMISSION_REQ_ID_RECORD_AUDIO = 22
const val NO_MIC_PERMISSIONS_DRAWABLE = R.drawable.ic_action_name
const val MIC_PERMISSIONS_ENABLED_DRAWABLE = R.drawable.ic_unmute

open class LobbyActivity : MyAppCompatActivity(){

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
    private var noSing : Boolean = false
    private var hasVoiceIdPermissions : Boolean = false
    private var gameLobbyId: Long = -1L
    private var singerDuration: Long = DEFAULT_SINGER_DURATION

    private lateinit var uuidTextView     : TextView
    private lateinit var hostNameTextView : TextView
    private lateinit var gameNameTextView : TextView
    private lateinit var playlistTextView : TextView
    private lateinit var nbRoundsTextView : TextView
    private lateinit var withHintTextView : TextView
    private lateinit var privateTextView  : TextView
    private lateinit var noSingTextView  : TextView
    private lateinit var singerDurationTextView : TextView

    private lateinit var launchGameButton: Button
    private lateinit var askForPermissionsButton: Button
    private lateinit var displayQRCodeButton: ImageButton

    private lateinit var qrCodeDisplay: DialogFragment

    private var layoutManager:RecyclerView.LayoutManager? = null
    private var layoutAdapter: RecyclerLobbyAdapter? = null

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
            initQRCode()
        }
    }

    private fun initLobbyAsHost() {
        db.getLobbyId().addOnSuccessListener { uid ->
            gameLobbyId = uid
            uuidTextView.text = getString(R.string.uid_text_format, uid)
            linkToDatabase(uid)
            initQRCode()

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
                            goToGameActivity(true, game, uid, singerDuration)

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
        noSing = intent.getBooleanExtra(GAME_IS_NO_SING_MODE, false)

        // if user is not the host, get the name from the intent
        hostName = if (isHost) user.name else intent.getStringExtra(GAME_HOST_NAME_KEY)

        playlistName = playlist?.name
                // if playlist is null, get the name from the intent
            ?: intent.getStringExtra(GAME_PLAYLIST_NAME_KEY)

        gameName        = intent.getStringExtra(GAME_NAME_KEY)
        nbRounds        = intent.getIntExtra(GAME_NB_ROUNDS_KEY, getString(R.string.default_game_nb_rounds).toInt())
        withHint        = intent.getBooleanExtra(GAME_HINT_KEY, false)
        isPrivate       = intent.getBooleanExtra(GAME_PRIVACY_KEY, false)
        singerDuration    = intent.getLongExtra(GAME_DURATION_KEY, DEFAULT_SINGER_DURATION)

        hasVoiceIdPermissions = checkPermission()
        gameLobbyId = intent.getLongExtra(GAME_UID_KEY, -1L)
    }


    private fun checkPermission(): Boolean {
        val audio_permission = ContextCompat.checkSelfPermission(this,
            Manifest.permission.RECORD_AUDIO
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val connect_permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH_CONNECT
            )
            val scan_permission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH_SCAN
            )
            return audio_permission == PackageManager.PERMISSION_GRANTED && connect_permission == PackageManager.PERMISSION_GRANTED && scan_permission == PackageManager.PERMISSION_GRANTED
        }

        return audio_permission == PackageManager.PERMISSION_GRANTED
    }

    private fun initTextViews() {
        uuidTextView     = findViewById(R.id.uuidText)
        hostNameTextView = findViewById(R.id.hostNameText)
        gameNameTextView = findViewById(R.id.gameNameText)
        playlistTextView = findViewById(R.id.playlistText)
        nbRoundsTextView = findViewById(R.id.nbRoundsText)
        withHintTextView = findViewById(R.id.withHintText)
        privateTextView  = findViewById(R.id.privateText)
        noSingTextView   = findViewById(R.id.noSingText)
        singerDurationTextView = findViewById(R.id.singerDurationLobbyText)
        layoutManager = LinearLayoutManager(this)
        val recyclerView = findViewById<RecyclerView>(R.id.lobbyRecyclerView)
        recyclerView.layoutManager = layoutManager
        layoutAdapter = RecyclerLobbyAdapter()
        recyclerView.adapter = layoutAdapter
        if (isHost) {
            val hostMicPermissionsImg = if (hasVoiceIdPermissions) MIC_PERMISSIONS_ENABLED_DRAWABLE else NO_MIC_PERMISSIONS_DRAWABLE
            layoutAdapter?.setContent(arrayListOf(Pair(user.id, user.name)), intArrayOf(hostMicPermissionsImg))
            layoutAdapter?.notifyDataSetChanged()
        }
    }

    private fun initButtons() {
        launchGameButton = findViewById(R.id.launchGameButton)
        askForPermissionsButton = findViewById(R.id.micPermissionsButton)
        displayQRCodeButton = findViewById(R.id.displayQRCodeButton)
        if (!isHost) {
            launchGameButton.visibility = View.INVISIBLE
        }
        if (hasVoiceIdPermissions) {
            askForPermissionsButton.visibility = View.INVISIBLE
        }
        else {
            launchGameButton.isEnabled = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                askForPermissionsButton.setOnClickListener {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO,Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT), PERMISSION_REQ_ID_RECORD_AUDIO)
                }
            }else{
                askForPermissionsButton.setOnClickListener {
                    ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), PERMISSION_REQ_ID_RECORD_AUDIO)
                }
            }
        }
    }

    private fun initQRCode(){
        qrCodeDisplay = CreateQRCodeFragment(this, gameLobbyId)
        displayQRCodeButton.setOnClickListener{
            qrCodeDisplay.show(supportFragmentManager, "qrCodeForJoiningGame")
        }

    }

    private fun updateTextViews() {
        hostNameTextView.text = getString(R.string.host_name_format, hostName ?: "")
        gameNameTextView.text = getString(R.string.game_name_format, gameName)
        playlistTextView.text = getString(R.string.playlist_format, playlistName ?: "")
        nbRoundsTextView.text = getString(R.string.number_of_rounds_format, nbRounds)
        withHintTextView.text = getString(R.string.hints_enabled_format, withHint)
        privateTextView.text  = getString(R.string.private_format, isPrivate)
        singerDurationTextView.text = getString(R.string.gameDurationFormat, singerDuration)
        noSingTextView.text = getString(R.string.with_singer_format, !noSing)
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
                    .setNoSing(noSing)
                    .setDuration(singerDuration)


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
                val gameStillValid = snapshot.getAndCast<Boolean>(VALIDITY_KEY)
                if (!gameStillValid) {
                    returnToMainMenu()
                }

                val newMap = snapshot.getPlayers()

                val isGameLaunched = snapshot.getBoolean(LAUNCHED_KEY)

                val mapIdToPermissions = snapshot.getPermissions()
                val atLeastOnePermissionMissing = mapIdToPermissions.containsValue(false)
                launchGameButton.isEnabled = !atLeastOnePermissionMissing

                updateLobbyWithPlayers(newMap, mapIdToPermissions)


                if (!isHost && isGameLaunched != null && isGameLaunched) {
                    goToGameActivity(false, gameID = uid, singerDuration = singerDuration)
                }

            } else {
                uuidTextView.text = getString(R.string.uid_error)
            }

        }
    }

    private fun updateLobbyWithPlayers(playersMap: Map<String, String>, nameToPermissions: Map<String, Boolean>) {
        val micPermissions = arrayListOf<Int>()
        val users = arrayListOf<Pair<String, String>>()
        for ((userId, userName) in playersMap) {
            if (nameToPermissions[userId]!!) {
                micPermissions.add(MIC_PERMISSIONS_ENABLED_DRAWABLE)
            } else {
                micPermissions.add(NO_MIC_PERMISSIONS_DRAWABLE)
            }
            users.add(Pair(userId, userName))
        }
        layoutAdapter?.setContent(users, micPermissions.toIntArray())
        layoutAdapter?.notifyDataSetChanged()

        gameBuilder.setUserIdList(playersMap.keys)
        mapIdToName = HashMap(playersMap)
    }

    protected open fun goToGameActivity(isHost: Boolean, game: Game? = null, gameID: Long, singerDuration: Long = DEFAULT_SINGER_DURATION) {
        val intent: Intent = Intent(this, GameActivity::class.java).apply {
            putExtra(GAME_IS_HOST_KEY, isHost)
            putExtra(MAP_ID_NAME_KEY, mapIdToName)
            putExtra(GAME_UID_KEY, gameID)
            putExtra(GAME_DURATION_KEY, singerDuration)
            putExtra(GAME_IS_NO_SING_MODE, noSing)
            putExtra(GAME_HINT_KEY, withHint)
        }

        if (isHost && game != null) {
            intent.putExtra(GAME_KEY, Json.encodeToString(game))
        }
        startActivity(intent)
        finish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Test if received callback indeed comes from a microphone permission request


        if (grantResults.isNotEmpty()) {
            val audioAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val scanAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED
                val connectAccepted  = grantResults[2] == PackageManager.PERMISSION_GRANTED
                if (audioAccepted && connectAccepted && scanAccepted) {
                    validatePermissionsAccept()
                }
            }else{
                if (audioAccepted ){
                    validatePermissionsAccept()
                }
            }


        }
    }
    private fun validatePermissionsAccept() {
        hasVoiceIdPermissions = true
        launchGameButton.isEnabled = true
        askForPermissionsButton.visibility = View.INVISIBLE
        db.modifyUserMicPermissions(gameLobbyId, user, true)
    }

    override fun onBackPressed() {
        val warningDisplay = QuitGameOrLobbyDialog(this)
        warningDisplay.show(supportFragmentManager, "warningForQuittingLobby")
        supportFragmentManager
            .setFragmentResultListener("quitRequest", this) { _, bundle ->
                val hasQuit = bundle.getBoolean("hasQuit")
                if (hasQuit) {
                    returnToMainMenu()
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        db.removeLobbyListener()
    }

    private fun returnToMainMenu(){
        if (isHost) {
            db.disableLobby(gameLobbyId)
        }
        else {
            db.removeUserFromLobby(gameLobbyId, user)
        }
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

}
