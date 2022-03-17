package com.github.fribourgsdp.radio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class LobbyActivity : AppCompatActivity() {

    private val uuid = createUUID()

    private lateinit var uuidTextView : TextView
    private lateinit var hostNameTextView : TextView
    private lateinit var gameNameTextView : TextView
    private lateinit var playlistTextView : TextView
    private lateinit var nbRoundsTextView : TextView
    private lateinit var withHintTextView : TextView
    private lateinit var privateTextView : TextView

    private val gameBuilder = Game.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lobby)
        val hostName        = intent.getStringExtra(GAME_HOST_KEY)
        val gameName        = intent.getStringExtra(GAME_NAME_KEY)
        val playlistName    = intent.getStringExtra(GAME_PLAYLIST_KEY)
        val nbRounds        = intent.getIntExtra(GAME_NB_ROUNDS_KEY, getString(R.string.default_game_nb_rounds).toInt())
        val withHint        = intent.getBooleanExtra(GAME_HINT_KEY, false)
        val isPrivate       = intent.getBooleanExtra(GAME_PRIVACY_KEY, false)

        initTextViews()

        uuidTextView.text     = getString(R.string.uuid_text_format, uuid)
        hostNameTextView.text = getString(R.string.host_name_format, hostName)
        gameNameTextView.text = getString(R.string.game_name_format, gameName)
        playlistTextView.text = getString(R.string.playlist_format, playlistName)
        nbRoundsTextView.text = getString(R.string.number_of_rounds_format, nbRounds)
        withHintTextView.text = getString(R.string.hints_enabled_format, withHint)
        privateTextView.text  = getString(R.string.private_format, isPrivate)

        if (hostName != null && gameName != null && playlistName != null) {
            gameBuilder.setHost(User(hostName)).setName(gameName).setPlaylist(Playlist(playlistName)).setNbRounds(nbRounds).setWithHint(withHint).setPrivacy(isPrivate)
        }
    }

    private fun createUUID() : Int {
        // TODO: When we will be able to connect to the server, this will get the key and increment it by 1.
        return 1
    }

    private fun initTextViews() {
        uuidTextView     = findViewById(R.id.uuidText)
        hostNameTextView = findViewById(R.id.hostNameText)
        gameNameTextView = findViewById(R.id.gameNameText)
        playlistTextView = findViewById(R.id.playlistText)
        nbRoundsTextView = findViewById(R.id.nbRoundsText)
        withHintTextView = findViewById(R.id.withHintText)
        privateTextView  = findViewById(R.id.privateText)
    }
}