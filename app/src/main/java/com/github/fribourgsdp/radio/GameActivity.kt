package com.github.fribourgsdp.radio

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.agora.rtc.Constants
import io.agora.rtc.RtcEngine
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.math.absoluteValue


const val SCORES_KEY = "com.github.fribourgsdp.radio.SCORES"

open class GameActivity : AppCompatActivity(), GameView {
    private lateinit var user: User
    private var isHost: Boolean = false

    private lateinit var currentRoundTextView : TextView
    private lateinit var singerTextView : TextView
    private lateinit var songTextView : TextView
    private lateinit var errorOrFailureTextView : TextView
    private lateinit var lyricsPopup : PopupWindow
    private lateinit var songGuessEditText : EditText
    private lateinit var muteButton : ImageButton
    private lateinit var songGuessSubmitButton: Button
    private lateinit var showLyricsButton: Button

    private lateinit var scoresRecyclerView: RecyclerView
    private val scoresAdapter = ScoresAdapter()

    private lateinit var mapIdToName: HashMap<String, String>
    protected lateinit var voiceChannel: VoiceIpEngineDecorator
    lateinit var playerGameHandler: PlayerGameHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        user = User.load(this)
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
        playerGameHandler = PlayerGameHandler(gameUid, this)

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

        showLyricsButton.visibility = View.VISIBLE

        // Show the song instead
        songTextView.apply {
            text = songName
            visibility = View.VISIBLE
        }
    }

    override fun displayGuessInput() {
        // Hide the song view
        songTextView.visibility = View.GONE

        showLyricsButton.visibility = View.GONE

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

    override fun displayPlayerScores(playerScores: Map<String, Long>) {
        scoresAdapter.updateScore(
            // Replace ids by names
            playerScores.map { (id, score) -> Pair(mapIdToName[id] ?: id, score)}
        )
    }

    override fun gameOver(finalScores: Map<String, Long>) {
        val intent = Intent(this, EndGameActivity::class.java).apply {
            putExtra(SCORES_KEY,
                // Replace ids by names and put in an ArrayList to make it Serializable
                ArrayList(finalScores.map { (id, score) -> Pair(mapIdToName[id] ?: id, score)})
            )
        }
        startActivity(intent)
    }

    private fun returnToMainMenu() {

        if (isHost) {
            playerGameHandler.disableGame()
        }
        else {
            playerGameHandler.removeUserFromLobby(user)
            playerGameHandler.removePlayerFromGame(user)
        }
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()

    }

    private fun initViews() {
        currentRoundTextView = findViewById(R.id.currentRoundView)
        singerTextView = findViewById(R.id.singerTextView)
        songTextView = findViewById(R.id.songTextView)
        errorOrFailureTextView = findViewById(R.id.errorOrFailureTextView)

        scoresRecyclerView = findViewById(R.id.scoresRecyclerView)
        scoresRecyclerView.layoutManager = LinearLayoutManager(this)
        scoresRecyclerView.adapter = scoresAdapter

        songGuessEditText = findViewById(R.id.songGuessEditText)
        songGuessSubmitButton = findViewById(R.id.songGuessSubmitButton)
        showLyricsButton = findViewById(R.id.showLyricsButton)

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

    protected open fun initVoiceChat(gameUid: Long) {

        val map = mapIdToName.mapKeys { it.hashCode().absoluteValue }
        if (!this::voiceChannel.isInitialized) voiceChannel = VoiceIpEngineDecorator(this, MyIRtcEngineEventHandler(this, map))
        val userId = user.name.hashCode().absoluteValue
        voiceChannel.setAudioProfile(Constants.AUDIO_PROFILE_MUSIC_STANDARD, Constants.AUDIO_SCENARIO_CHATROOM_ENTERTAINMENT);
        voiceChannel.enableAudioVolumeIndication(200,3,true)
        voiceChannel.joinChannel(voiceChannel.getToken(userId, gameUid.toString()), gameUid.toString(), "", userId)
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
    override fun displayLyrics(lyrics : String) {
        showLyricsButton.visibility = View.VISIBLE
        showLyricsButton.setOnClickListener { displayLyrics(lyrics) }
        val lyricsPopup = LyricsPopup(lyrics)
        lyricsPopup.show(supportFragmentManager, "lyricsPopup")
    }
}
