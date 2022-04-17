package com.github.fribourgsdp.radio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import io.agora.rtc.Constants
import io.agora.rtc.RtcEngine
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.math.absoluteValue
import kotlin.random.Random


open class GameActivity : AppCompatActivity(), GameView, User.Loader {
    private lateinit var user: User
    private var isHost: Boolean = false

    private lateinit var currentRoundTextView : TextView
    private lateinit var singerTextView : TextView
    private lateinit var songTextView : TextView
    private lateinit var errorOrFailureTextView : TextView
    private lateinit var songGuessEditText : EditText
    private lateinit var muteButton : ImageButton
    private lateinit var songGuessSubmitButton: Button

    private lateinit var playersListView : ListView
    private lateinit var namesAdapter : ArrayAdapter<String>

    private lateinit var mapIdToName: HashMap<String, String>
    protected lateinit var voiceChannel: VoiceIpEngineDecorator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        user = loadUser()
        mapIdToName = intent.getSerializableExtra(MAP_ID_NAME_KEY)?.let {
            it as HashMap<String, String>
        } ?: HashMap()
        isHost = intent.getBooleanExtra(GAME_IS_HOST_KEY, false)
        initViews()

        val gameUid = intent.getLongExtra(GAME_UID_KEY, -1L)
        initVoiceChat(gameUid)

        if (isHost) {
            val game = Json.decodeFromString(intent.getStringExtra(GAME_KEY)!!) as Game
            val hostGameHandler = HostGameHandler(game, this)
            hostGameHandler.linkToDatabase()
        }
        val playerGameHandler = PlayerGameHandler(gameUid, this)

        // On submit make the player game handler handle the guess
        songGuessSubmitButton.setOnClickListener {
            playerGameHandler.handleGuess(songGuessEditText.text.toString(), user.id)
        }

        playerGameHandler.linkToDatabase()

    }

    override fun onDestroy() {
        super.onDestroy()
        voiceChannel.leaveChannel()
        RtcEngine.destroy()
    }

    override fun chooseSong(choices: List<String>, listener: GameView.OnPickListener) {
        val songPicker = SongPickerDialog(choices, listener)
        songPicker.show(supportFragmentManager, "songPicker")
    }

    override fun updateSinger(singerId: String) {
        singerTextView.text = getString(R.string.singing_format, mapIdToName[singerId] ?: singerId)
    }

    override fun updateRound(currentRound: Long) {
        currentRoundTextView.text = getString(R.string.current_round_format, currentRound)
    }

    override fun displaySong(songName: String) {
        // Hide the edit text and the submit button
        songGuessEditText.visibility = View.GONE
        songGuessSubmitButton.visibility = View.GONE

        // Show the song instead
        songTextView.apply {
            text = songName
            visibility = View.VISIBLE
        }
    }

    override fun displayGuessInput() {
        // Hide the song view
        songTextView.visibility = View.GONE

        // Show the edit text and the submit button instead
        songGuessEditText.apply {
            text.clear()
            visibility = View.VISIBLE
        }
        songGuessSubmitButton.visibility = View.VISIBLE
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
        return user.id == id
    }

    override fun displayWaitOnSinger(singer: String) {
        // We can display the wait message where the same box as the song
        displaySong(getString(R.string.wait_for_pick_format, mapIdToName[singer]  ?: singer))
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
        songGuessSubmitButton = findViewById(R.id.songGuessSubmitButton)

        // trigger the submit button when the user presses "enter" in the text field
        songGuessEditText.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                songGuessSubmitButton.callOnClick()
                return@setOnEditorActionListener true
            }
            false
        }
        muteButton = findViewById<ImageButton>(R.id.muteChannelButton)
        muteButton.setOnClickListener {
            voiceChannel.mute(muteButton)
        }
    }

    open protected fun initVoiceChat(gameUid: Long) {

        val map = mapIdToName.mapKeys { it.hashCode().absoluteValue }
        if (!this::voiceChannel.isInitialized) voiceChannel = VoiceIpEngineDecorator(this, MyIRtcEngineEventHandler(this, map))
        val userId = user.name.hashCode().absoluteValue
        voiceChannel.setAudioProfile(Constants.AUDIO_PROFILE_MUSIC_STANDARD, Constants.AUDIO_SCENARIO_CHATROOM_ENTERTAINMENT);
        voiceChannel.enableAudioVolumeIndication(200,3,true)
        voiceChannel.joinChannel(voiceChannel.getToken(userId, gameUid.toString()), gameUid.toString(), "", userId)
    }

    override fun loadUser(): User {
        return User.load(this)
    }

}