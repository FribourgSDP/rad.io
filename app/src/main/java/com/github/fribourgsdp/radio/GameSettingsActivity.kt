package com.github.fribourgsdp.radio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val GAME_HOST_KEY = "com.github.fribourgsdp.radio.GAME_HOST"
const val GAME_NAME_KEY = "com.github.fribourgsdp.radio.GAME_NAME"
const val GAME_PLAYLIST_KEY = "com.github.fribourgsdp.radio.GAME_PLAYLIST"
const val GAME_NB_ROUNDS_KEY = "com.github.fribourgsdp.radio.GAME_NB_ROUNDS"
const val GAME_HINT_KEY = "com.github.fribourgsdp.radio.GAME_HINT"
const val GAME_PRIVACY_KEY = "com.github.fribourgsdp.radio.GAME_PRIVACY"
const val GAME_IS_HOST_KEY = "com.github.fribourgsdp.radio.GAME_IS_HOST"

class GameSettingsActivity : AppCompatActivity() {
    private val json = Json {
        allowStructuredMapKeys = true
    }
    private val host = getHost()

    private lateinit var nameInput : EditText
    private lateinit var nbRoundsInput : EditText
    private lateinit var hintCheckBox : CheckBox
    private lateinit var privacyCheckBox : CheckBox
    private lateinit var startButton : Button

    private lateinit var playlistSearchView : SearchView
    private lateinit var playlistListView : ListView
    private lateinit var playlistsNames : Array<String>
    private lateinit var playlistAdapter : ArrayAdapter<String>
    private lateinit var errorText : TextView

    private lateinit var selectedPlaylist: Playlist

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_settings)

        nameInput = findViewById(R.id.nameInput)
        nbRoundsInput = findViewById(R.id.nbRoundsInput)
        hintCheckBox = findViewById(R.id.hintCheckBox)
        privacyCheckBox = findViewById(R.id.privacyCheckBox)
        startButton = findViewById(R.id.startButton)
        playlistSearchView = findViewById(R.id.playlistSearchView)
        playlistListView = findViewById(R.id.playlistListView)
        playlistsNames = getUserPlaylistNames(host)
        playlistAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, playlistsNames)
        errorText = findViewById(R.id.playlistSearchError)

        playlistListView.adapter = playlistAdapter


        // When user is clicks on the bar, show the possibilities
        playlistSearchView.setOnSearchClickListener { playlistListView.visibility = View.VISIBLE }

        playlistSearchView.setOnQueryTextListener(searchViewOnQueryBehavior())

        playlistListView.setOnItemClickListener{ parent, _, position, _ ->
            val name = parent.getItemAtPosition(position).toString()
            selectedPlaylist = getPlaylist(name)
            playlistSearchView.setQuery(name, true)
            startButton.isEnabled = true
        }

        startButton.setOnClickListener(startButtonBehavior())

    }

    private fun getHost() : User {
        // TODO: Update once we can get the user of the phone
        val host = User("The best player")
        host.addPlaylists(
            setOf(
                Playlist("Rap Playlist"),
                Playlist(
                    "French Playlist",
                    setOf(
                        Song("Rouge", "Sardou"),
                        Song("La chenille", "La Bande à Basile"),
                        Song("Allume le feu", "Johnny Hallyday"),
                        Song("Que Je T'aime", "Johnny Hallyday")
                    ),
                    Genre.NONE
                ),
                Playlist("Country Playlist")
            )
        )
        return host
    }

    private fun getUserPlaylistNames(user: User) : Array<String> {
        return user.getPlaylists()
            .map { x -> x.name }
            .toTypedArray()
    }

    private fun startButtonBehavior() : View.OnClickListener {
        return View.OnClickListener {
            val intent: Intent = Intent(this, LobbyActivity::class.java).apply {
                putExtra(GAME_HOST_KEY, json.encodeToString(host))
                putExtra(GAME_NAME_KEY, nameInput.text.toString().ifEmpty { getString(R.string.default_game_name) })
                putExtra(GAME_PLAYLIST_KEY, json.encodeToString(selectedPlaylist))
                putExtra(GAME_NB_ROUNDS_KEY, nbRoundsInput.text.toString().ifEmpty { getString(R.string.default_game_nb_rounds) }.toInt())
                putExtra(GAME_HINT_KEY, hintCheckBox.isChecked)
                putExtra(GAME_PRIVACY_KEY, privacyCheckBox.isChecked)
                putExtra(GAME_IS_HOST_KEY, true)
            }
            startActivity(intent)
        }
    }

    private fun searchViewOnQueryBehavior() : SearchView.OnQueryTextListener {
        return object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // When selected, hide the possibilities
                playlistListView.visibility = View.GONE

                if (playlistsNames.contains(query) && query != null) {
                    playlistAdapter.filter.filter(query)
                    selectedPlaylist = getPlaylist(query)
                    startButton.isEnabled = true
                    errorText.visibility = View.GONE
                } else {
                    errorText.text = getString(R.string.playlist_error_format, query)
                    errorText.visibility = View.VISIBLE
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

    private fun getPlaylist(name: String): Playlist {
        return host.getPlaylists().find { playlist -> playlist.name == name }!!
    }
}