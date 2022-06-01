package com.github.fribourgsdp.radio.game

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.fribourgsdp.radio.MainActivity
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.external.musixmatch.MusixmatchLyricsGetter.LYRICS_NOT_FOUND_PLACEHOLDER
import com.github.fribourgsdp.radio.game.handler.HostGameHandler
import com.github.fribourgsdp.radio.game.handler.PlayerGameHandler
import com.github.fribourgsdp.radio.game.prep.*
import com.github.fribourgsdp.radio.game.timer.Timer
import com.github.fribourgsdp.radio.game.timer.TimerProgressBarHandler
import com.github.fribourgsdp.radio.game.view.*
import com.github.fribourgsdp.radio.util.MyTextToSpeech
import com.github.fribourgsdp.radio.util.SongNameHint
import com.github.fribourgsdp.radio.voip.MyIRtcEngineEventHandler
import com.github.fribourgsdp.radio.voip.VoiceIpEngineDecorator
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.agora.rtc.Constants
import io.agora.rtc.RtcEngine
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import java.util.*
import kotlin.math.absoluteValue
import kotlin.math.ceil


const val SCORES_KEY = "com.github.fribourgsdp.radio.SCORES"
const val GAME_CRASH_KEY = "com.github.fribourgsdp.radio.GAME_CRASH"
const val HINT_FRACTION = 1.0/3

open class GameActivity : AppCompatActivity(), GameView, Timer.Listener {
    private lateinit var user: User
    private var isHost: Boolean = false
    private var noSing = false
    private var gameDuration = DEFAULT_SINGER_DURATION

    private lateinit var currentRoundTextView : TextView
    private lateinit var singerTextView : TextView
    private lateinit var songTextView : TextView
    private lateinit var hintTextView : TextView
    private lateinit var errorOrFailureTextView : TextView
    private var lyricsPopup : LyricsPopup? = null
    private var cantQuitGamePopup : CannotQuitDialog? = null
    private lateinit var songGuessEditText : EditText
    private lateinit var muteButton : FloatingActionButton
    private lateinit var songGuessSubmitButton: Button
    private lateinit var timerProgressBarHandler: TimerProgressBarHandler
    private lateinit var showLyricsButton: Button

    private lateinit var scoresRecyclerView: RecyclerView
    private val scoresAdapter = ScoresAdapter()
    private var withHint = false
    private lateinit var songNameHint : SongNameHint
    private var lastTime = 0

    private lateinit var mapIdToName: HashMap<String, String>
    protected lateinit var voiceChannel: VoiceIpEngineDecorator

    private lateinit var playerGameHandler: PlayerGameHandler
    private lateinit var hostGameHandler : HostGameHandler

    private lateinit var tts : MyTextToSpeech

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)
        user = User.load(this)
        mapIdToName = intent.getSerializableExtra(MAP_ID_NAME_KEY)?.let {
            it as HashMap<String, String>
        } ?: HashMap()
        isHost = intent.getBooleanExtra(GAME_IS_HOST_KEY, false)
        noSing = intent.getBooleanExtra(GAME_IS_NO_SING_MODE, false)
        gameDuration = intent.getLongExtra(GAME_DURATION_KEY, DEFAULT_SINGER_DURATION)

        withHint = intent.getBooleanExtra(GAME_HINT_KEY, false)
        initViews()

        val gameUid = intent.getLongExtra(GAME_UID_KEY, -1L)
        initVoiceChat(gameUid)

        initHostGameHandler()
        tts = MyTextToSpeech(applicationContext)
        tts.initTextToSpeech(noSing)

        playerGameHandler = PlayerGameHandler(this, gameUid, this, noSing=noSing, tts=tts)
        playerGameHandler.setSingerDuration(gameDuration)

        // On submit make the player game handler handle the guess
        songGuessSubmitButton.setOnClickListener {
            playerGameHandler.handleGuess(songGuessEditText.text.toString(), user.id)
        }

        playerGameHandler.linkToDatabase()

    }

    override fun onDestroy() {
        super.onDestroy()
        playerGameHandler.unlinkFromDatabase()
        if(isHost) {
            hostGameHandler.unlinkFromDatabase()
        }
        voiceChannel.leaveChannel()
        RtcEngine.destroy()
    }

    override fun chooseSong(choices: List<String>, listener: GameView.OnPickListener) {
        val songPicker = SongPickerDialog(choices, listener)
        songPicker.show(supportFragmentManager, "songPicker")
    }

    override fun updateSinger(singerId: String) {
        if(noSing){
            singerTextView.text = getString(R.string.listen)
        } else {
            singerTextView.text =
                getString(R.string.singing_format, mapIdToName[singerId] ?: singerId)
        }
    }

    override fun updateRound(currentRound: Long) {
        currentRoundTextView.text = getString(R.string.current_round_format, currentRound)
    }

    override fun displaySong(songName: String) {
        //Close any active popup if open
        closePopups()

        // Hide the edit text and the submit button
        if(!noSing) {
            songGuessEditText.visibility = View.GONE
            songGuessSubmitButton.visibility = View.GONE
        }

        if (lyricsPopup != null) {
            showLyricsButton.visibility = View.VISIBLE
        }

        // Show the song instead
        songTextView.apply {
            text = songName
            visibility = View.VISIBLE
        }

        if(withHint) {
            hintTextView.visibility = View.GONE
        }
    }

    override fun displayGuessInput() {
        //Close any active popup if open
        closePopups()

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
        //Close any active popup if open
        closePopups()

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
        showLyricsButton.visibility = View.GONE
    }

    override fun displayPlayerScores(playerScores: Map<String, Long>) {
        //Close any active popup if open
        closePopups()

        scoresAdapter.updateScore(
            // Replace ids by names
            playerScores.map { (id, score) -> Pair(mapIdToName[id] ?: id, score)}
        )
    }

    override fun startTimer(deadline: Date) {
        timerProgressBarHandler.startTimer(deadline)
    }

    override fun stopTimer() {
        timerProgressBarHandler.stopTimer()
    }

    override fun gameOver(finalScores: Map<String, Long>?, hasCrashed: Boolean) {
        val intent = Intent(this, EndGameActivity::class.java).apply {
            finalScores?.let {
                putExtra(
                    SCORES_KEY,
                    // Replace ids by names and put in an ArrayList to make it Serializable
                    ArrayList(it.map { (id, score) -> Pair(mapIdToName[id] ?: id, score) })
                )
            }

            putExtra(GAME_CRASH_KEY, hasCrashed)
        }
        startActivity(intent)
        finish()
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

    private fun initHostGameHandler(){
        if (isHost) {
            val game = Json.decodeFromString(intent.getStringExtra(GAME_KEY)!!) as Game
            hostGameHandler = HostGameHandler(this, game, this, noSing=noSing)
            hostGameHandler.linkToDatabase()
            hostGameHandler.setSingerDuration(gameDuration)
        }
    }

    private fun initViews() {
        currentRoundTextView = findViewById(R.id.currentRoundView)
        singerTextView = findViewById(R.id.singerTextView)
        songTextView = findViewById(R.id.songTextView)
        hintTextView = findViewById(R.id.hintTextView)
        errorOrFailureTextView = findViewById(R.id.errorOrFailureTextView)

        scoresRecyclerView = findViewById(R.id.scoresRecyclerView)
        scoresRecyclerView.layoutManager = LinearLayoutManager(this)
        scoresRecyclerView.adapter = scoresAdapter

        songGuessEditText = findViewById(R.id.songGuessEditText)
        songGuessSubmitButton = findViewById(R.id.songGuessSubmitButton)
        showLyricsButton = findViewById(R.id.showLyricsButton)
        showLyricsButton.setOnClickListener { lyricsPopup?.show(supportFragmentManager, "lyricsPopup") }

        // trigger the submit button when the user presses "enter" in the text field
        songGuessEditText.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                songGuessSubmitButton.callOnClick()
                return@setOnEditorActionListener true
            }
            false
        }
        muteButton = findViewById(R.id.muteChannelButton)
        muteButton.setOnClickListener {
            voiceChannel.mute()

            val newIcon = if (voiceChannel.isMuted) R.drawable.ic_mute else R.drawable.ic_unmute
            muteButton.apply {
                setImageResource(newIcon)
                // we set the tag to have a trace of which drawable is used
                tag = newIcon
            }
        }

        // Init for the TimerProgressBarHolder
        timerProgressBarHandler = TimerProgressBarHandler(Timer(), findViewById(R.id.roundProgressBar), this)
    }

    protected open fun initVoiceChat(gameUid: Long) {
        val map = mapIdToName.mapKeys { it.key.hashCode().absoluteValue }
        if (!this::voiceChannel.isInitialized) voiceChannel = VoiceIpEngineDecorator(this, MyIRtcEngineEventHandler(this, map))
        val userId = user.id.hashCode().absoluteValue
        voiceChannel.setAudioProfile(Constants.AUDIO_PROFILE_MUSIC_STANDARD, Constants.AUDIO_SCENARIO_CHATROOM_ENTERTAINMENT);
        voiceChannel.enableAudioVolumeIndication(200,3,true)
        voiceChannel.setDefaultAudioRoutetoSpeakerphone(true)
        voiceChannel.joinChannel(voiceChannel.getToken(userId, gameUid.toString()), gameUid.toString(), "", userId)
    }

    override fun onDone() {
        // Run on main thread
        runOnUiThread {
            // When the timer is done it means the user didn't guess in time
            // We display this in the same box as the sound so that is hides the guess input view
            displaySong(getString(R.string.round_done))
            playerGameHandler.handleGuess("", user.id, true)
        }
    }

    override fun onUpdate(timeInSeconds: Long) {

        updateHint(timeInSeconds.toInt())

        // Run on main thread
        runOnUiThread {
            timerProgressBarHandler.progressBar.setProgress(timeInSeconds.toInt(), true)
        }
    }

    override fun onBackPressed() {
        if (isHost) {
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
        else {
            cantQuitGamePopup = CannotQuitDialog(this)
            cantQuitGamePopup?.show(supportFragmentManager, "cannotQuitGame")
        }
    }
    
    override fun updateLyrics(lyrics : String) {
        //Close any active popup if open
        closePopups()
        
        lyricsPopup = if(lyrics.isNotEmpty() && lyrics != LYRICS_NOT_FOUND_PLACEHOLDER)  LyricsPopup(lyrics)
            else null
    }

    override fun onPause() {
        tts.onPause()
        super.onPause()
    }

    override fun addHint(songNameHint: SongNameHint) {
        if(withHint) {
            hintTextView.visibility = View.VISIBLE
            this.songNameHint = songNameHint
            lastTime = 0
            hintTextView.text = this.songNameHint.toString()
        }
    }

    fun updateHint(timeInSeconds: Int){
        if(withHint && hintTextView.visibility == View.VISIBLE) {
            val limit = ceil(gameDuration/ (ceil(songNameHint.length()* HINT_FRACTION)+1))
            if(timeInSeconds - lastTime >= limit) {
                songNameHint.addALetter()
                hintTextView.text = this.songNameHint.toString()
                lastTime = timeInSeconds
            }
        }
    }

    private fun closePopups() {
        lyricsPopup?.let {
            if (it.isVisible) {
                it.dismiss()
            }
        }
        cantQuitGamePopup?.let {
            if (it.isVisible) {
                it.dismiss()
            }
        }
    }
}
