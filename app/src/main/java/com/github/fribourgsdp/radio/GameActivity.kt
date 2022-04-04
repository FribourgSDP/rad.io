package com.github.fribourgsdp.radio

import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class GameActivity : AppCompatActivity(), GameView {
    // TODO: Use 'User.load(this)' when available
    private var user = User("The second best player")
    private var isHost: Boolean = false

    private lateinit var currentRoundTextView : TextView
    private lateinit var singerTextView : TextView
    private lateinit var songTextView : TextView
    private lateinit var errorOrFailureTextView : TextView
    private lateinit var songGuessEditText : EditText

    private lateinit var playersListView : ListView
    private lateinit var namesAdapter : ArrayAdapter<String>

    private lateinit var playerGameHandler: PlayerGameHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        isHost = intent.getBooleanExtra(GAME_IS_HOST_KEY, false)
        initViews()
        if (isHost) {
            val json = Json {
                allowStructuredMapKeys = true
            }
            val game = json.decodeFromString(intent.getStringExtra(GAME_KEY)!!) as Game
            val hostGameHandler = HostGameHandler(game, this)
            hostGameHandler.linkToDatabase()
            user = User("The best player")
            playerGameHandler = PlayerGameHandler(game.id, this)
        } else {
            playerGameHandler = PlayerGameHandler(intent.getLongExtra(GAME_UID_KEY, -1L), this)
        }

        playerGameHandler.linkToDatabase()

    }

    override fun chooseSong(choices: List<String>, listener: GameView.OnPickListener) {
        val songPicker = SongPickerDialog(choices, listener)
        songPicker.show(supportFragmentManager, "songPicker")
    }

    override fun updateSinger(singerName: String) {
        singerTextView.text = getString(R.string.singing_format, singerName)
    }

    override fun updateRound(currentRound: Long) {
        currentRoundTextView.text = getString(R.string.current_round_format, currentRound)
    }

    override fun displaySong(songName: String) {
        // Hide the edit text
        songGuessEditText.visibility = View.GONE

        // Show the song instead
        songTextView.apply {
            text = songName
            visibility = View.VISIBLE
        }
    }

    override fun displayGuessInput() {
        // Hide the song view
        songTextView.visibility = View.GONE

        // Show the edit text instead
        songGuessEditText.visibility = View.VISIBLE
    }

    override fun displayError(errorMessage: String) {
        // Show the error
        errorOrFailureTextView.apply {
            text = errorMessage
            visibility = View.VISIBLE
        }
    }

    override fun hideError() {
        errorOrFailureTextView.visibility = View.GONE
    }

    override fun checkPlayer(id: String): Boolean {
        return user.name == id
    }

    private fun initViews() {
        currentRoundTextView = findViewById(R.id.currentRoundView)
        singerTextView = findViewById(R.id.singerTextView)
        songTextView = findViewById(R.id.songTextView)
        errorOrFailureTextView = findViewById(R.id.errorOrFailureTextView)
        // TODO: Initialise in later sprint
        // namesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        // playersListView.adapter = namesAdapter

        songGuessEditText = findViewById(R.id.songGuessEditText)

        // trigger the button when the user presses "enter" in the text field
        songGuessEditText.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                playerGameHandler.handleGuess(songGuessEditText.text.toString(), user.name)
                return@setOnEditorActionListener true
            }
            false
        }
    }

}