package com.github.fribourgsdp.radio.game.prep

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.akexorcist.snaptimepicker.SnapTimePickerDialog
import com.akexorcist.snaptimepicker.TimeRange
import com.akexorcist.snaptimepicker.TimeValue
import com.github.fribourgsdp.radio.MainActivity
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.game.Game
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val GAME_HOST_KEY = "com.github.fribourgsdp.radio.GAME_HOST"
const val GAME_NAME_KEY = "com.github.fribourgsdp.radio.GAME_NAME"
const val GAME_PLAYLIST_KEY = "com.github.fribourgsdp.radio.GAME_PLAYLIST"
const val GAME_NB_ROUNDS_KEY = "com.github.fribourgsdp.radio.GAME_NB_ROUNDS"
const val GAME_HINT_KEY = "com.github.fribourgsdp.radio.GAME_HINT"
const val GAME_PRIVACY_KEY = "com.github.fribourgsdp.radio.GAME_PRIVACY"
const val GAME_IS_HOST_KEY = "com.github.fribourgsdp.radio.GAME_IS_HOST"
const val GAME_IS_NO_SING_MODE = "com.github.fribourgsdp.radio.GAME_IS_NO_SING_MODE"
const val GAME_DURATION_KEY = "com.github.fribourgsdp.radio.GAME_DURATION"

const val DEFAULT_GAME_DURATION = 45L;

open class GameSettingsActivity : AppCompatActivity() {
    private lateinit var host: User

    private lateinit var nameInput : EditText
    private lateinit var nbRoundsInput : EditText
    private lateinit var hintCheckBox : CheckBox
    private lateinit var privacyCheckBox : CheckBox
    private lateinit var noSingCheckBox : CheckBox
    private lateinit var startButton : Button
    private lateinit var timerButton : Button

    private lateinit var playlistSearchView : SearchView
    private lateinit var playlistListView : ListView
    private lateinit var playlistsNames : Array<String>
    private lateinit var playlistAdapter : ArrayAdapter<String>
    private lateinit var errorText : TextView

    private lateinit var selectedPlaylist: Playlist

    private var gameTimeDuration = DEFAULT_GAME_DURATION

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_settings)

        host = User.load(this)

        initViews()
        initBehaviours()

    }

    private fun initBehaviours() {
        // When user is clicks on the bar, show the possibilities
        playlistSearchView.setOnSearchClickListener { playlistListView.visibility = View.VISIBLE }

        playlistSearchView.setOnQueryTextListener(SearchViewOnQueryBehavior())

        playlistListView.setOnItemClickListener{ parent, _, position, _ ->
            val name = parent.getItemAtPosition(position).toString()
            selectedPlaylist = getPlaylist(name)
            playlistSearchView.setQuery(name, true)
            startButton.isEnabled = true
        }

        startButton.setOnClickListener(startButtonBehavior())

        timerButton.setOnClickListener {
            SnapTimePickerDialog.Builder().apply {
                setTitle(R.string.timerSelectTime)
                setPrefix(R.string.timerMinutes)
                setSuffix(R.string.timerSeconds)
                setThemeColor(R.color.red)
                setPreselectedTime(TimeValue(0, DEFAULT_GAME_DURATION.toInt()))
                setSelectableTimeRange(TimeRange(TimeValue(0, 5), TimeValue(2, 0)))
            }.build().apply {
                setListener { minute, second ->
                    gameTimeDuration = (60*minute + second).toLong()
                    timerButton.text = getString(R.string.round_duration_format, gameTimeDuration)
                }
            }.show(supportFragmentManager, "TimerSettings")
        }
    }

    private fun initViews() {
        nameInput = findViewById(R.id.nameInput)
        nbRoundsInput = findViewById(R.id.nbRoundsInput)
        hintCheckBox = findViewById(R.id.hintCheckBox)
        privacyCheckBox = findViewById(R.id.privacyCheckBox)
        noSingCheckBox = findViewById(R.id.noSingCheckbox)
        startButton = findViewById(R.id.startButton)
        playlistSearchView = findViewById(R.id.playlistSearchView)
        playlistListView = findViewById(R.id.playlistListView)
        playlistsNames = getUserPlaylistNames(host)
        playlistAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, playlistsNames)
        errorText = findViewById(R.id.playlistSearchError)
        timerButton = findViewById<Button?>(R.id.chooseTimeButton).apply {
            text = getString(R.string.round_duration_format, gameTimeDuration)
        }

        playlistListView.adapter = playlistAdapter
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun getUserPlaylistNames(user: User) : Array<String> {
        return user.getPlaylists()
            .map { x -> x.name }
            .toTypedArray()
    }

    private fun displayError(error : String){
        errorText.text = error
        errorText.visibility = View.VISIBLE
    }

    private fun startButtonBehavior() : View.OnClickListener {
        return View.OnClickListener {
            if(noSingCheckBox.isChecked && selectedPlaylist.getSongs().none{s -> s.songHasLyrics()}){
                displayError(getString(R.string.playlistLyricsLess))
            } else {
                val intent: Intent = Intent(this, LobbyActivity::class.java).apply {
                    putExtra(
                        GAME_NAME_KEY,
                        nameInput.text.toString().ifEmpty { getString(R.string.default_game_name) })
                    putExtra(GAME_PLAYLIST_KEY, Json.encodeToString(selectedPlaylist))
                    putExtra(
                        GAME_NB_ROUNDS_KEY,
                        nbRoundsInput.text.toString()
                            .ifEmpty { getString(R.string.default_game_nb_rounds) }.toInt()
                    )
                    putExtra(GAME_HINT_KEY, hintCheckBox.isChecked)
                    putExtra(GAME_PRIVACY_KEY, privacyCheckBox.isChecked)
                    putExtra(GAME_IS_HOST_KEY, true)
                    putExtra(GAME_IS_NO_SING_MODE, noSingCheckBox.isChecked)
                    putExtra(GAME_DURATION_KEY, gameTimeDuration)
                }
                startActivity(intent)
            }
        }
    }
    private fun getPlaylist(name: String): Playlist {
        return host.getPlaylists().find { playlist -> playlist.name == name }!!
    }

    private inner class SearchViewOnQueryBehavior: SearchView.OnQueryTextListener {
        override fun onQueryTextSubmit(query: String?): Boolean {
            // When selected, hide the possibilities
            playlistListView.visibility = View.GONE

            if (playlistsNames.contains(query) && query != null) {
                playlistAdapter.filter.filter(query)
                selectedPlaylist = getPlaylist(query)
                startButton.isEnabled = true
                errorText.visibility = View.GONE
            } else {
                displayError(getString(R.string.playlist_error_format, query))
                startButton.isEnabled = false
            }

            playlistSearchView.clearFocus()

            return false

        }

        override fun onQueryTextChange(newText: String?): Boolean {
            // When user is typing, show the possibilities
            playlistListView.visibility = View.VISIBLE
            playlistAdapter.filter.filter(newText)
            startButton.isEnabled = false
            errorText.visibility = View.GONE
            return false
        }

    }
}
