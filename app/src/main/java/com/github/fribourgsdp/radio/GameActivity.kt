package com.github.fribourgsdp.radio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import io.agora.rtc.Constants
import io.agora.rtc.RtcEngine
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.random.Random

const val IS_IN_TEST_MODE = "Test mode for voice chat engine"

class GameActivity : AppCompatActivity(), GameView {
    //TODO: Use 'User.load(this)' when available -> should be ok now
    private var user = User("The second best player")
    //private lateinit var user: User
    private var isHost: Boolean = false

    private lateinit var currentRoundTextView : TextView
    private lateinit var singerTextView : TextView
    private lateinit var songTextView : TextView
    private lateinit var errorOrFailureTextView : TextView
    private lateinit var songGuessEditText : EditText
    private lateinit var muteButton : ImageButton

    private lateinit var playersListView : ListView
    private lateinit var namesAdapter : ArrayAdapter<String>

    private lateinit var playerGameHandler: PlayerGameHandler
    private lateinit var voiceChannel: VoiceIpEngineDecorator


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        isHost = intent.getBooleanExtra(GAME_IS_HOST_KEY, false)
        initViews()
        initVoiceChat(intent.getBooleanExtra(IS_IN_TEST_MODE, true), intent.getLongExtra(
            GAME_UID_KEY, 0))
        if (isHost) {
            val json = Json {
                allowStructuredMapKeys = true
            }
            val game = json.decodeFromString(intent.getStringExtra(GAME_KEY)!!) as Game
            val hostGameHandler = HostGameHandler(game, this)
            hostGameHandler.linkToDatabase()
            user = User("The best player")
        }
        playerGameHandler = PlayerGameHandler(intent.getLongExtra(GAME_UID_KEY, -1L), this)
        playerGameHandler.linkToDatabase()

    }

    override fun onDestroy() {
        super.onDestroy()
        voiceChannel.leaveChannel()
        RtcEngine.destroy()
    }

    override fun chooseSong(choices: List<String>): String {
        // TODO: For later sprint, implement a way to chose between songs
        return choices[0]
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
        muteButton = findViewById<ImageButton>(R.id.muteChannelButton)
        muteButton.setOnClickListener {
            voiceChannel.mute(muteButton)
        }
    }

    private fun initVoiceChat(is_test_mode: Boolean, gameUid: Long) {


        voiceChannel = VoiceIpEngineDecorator(this)

        val userId = Random.nextInt(100000000)
        voiceChannel.setAudioProfile(Constants.AUDIO_PROFILE_MUSIC_STANDARD, Constants.AUDIO_SCENARIO_CHATROOM_ENTERTAINMENT);
        voiceChannel.enableAudioVolumeIndication(500,5,true)
        voiceChannel.joinChannel(voiceChannel.getToken(userId, gameUid.toString()), gameUid.toString(), "", userId)
        Log.d("Game uid is: ", gameUid.toString())
    }

}