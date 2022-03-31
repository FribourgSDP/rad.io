package com.github.fribourgsdp.radio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView

class GameActivity : AppCompatActivity(), GameView {
    private val user = User.load(this)
    private var isHost: Boolean = false

    private lateinit var currentRoundTextView : TextView
    private lateinit var singerTextView : TextView
    private lateinit var songTextView : TextView
    private lateinit var songGuessEditText : EditText

    private lateinit var playersListView : ListView
    private lateinit var namesAdapter : ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        isHost = intent.getBooleanExtra(GAME_IS_HOST_KEY, false)

        initViews()

    }

    override fun chooseSong(choices: List<String>): String {
        TODO("Not yet implemented")
        // Show popup with 3 buttons
        // return song
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
        // Hide the edit text
        songGuessEditText.visibility = View.GONE

        // Show the error in the song TextView instead
        songTextView.apply {
            text = errorMessage
            visibility = View.VISIBLE
        }
    }

    override fun checkPlayer(id: String): Boolean {
        return user.name == id
    }

    private fun initViews() {
        currentRoundTextView = findViewById(R.id.currentRoundView)
        singerTextView = findViewById(R.id.singerTextView)
        songTextView = findViewById(R.id.songTextView)
        songGuessEditText = findViewById(R.id.songGuessEditText)
        namesAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        playersListView.adapter = namesAdapter
    }

}