package com.github.fribourgsdp.radio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*

const val GAME_HOST_KEY = "com.github.fribourgsdp.radio.GAME_HOST"
const val GAME_NAME_KEY = "com.github.fribourgsdp.radio.GAME_NAME"
const val GAME_PLAYLIST_KEY = "com.github.fribourgsdp.radio.GAME_PLAYLIST"
const val GAME_NB_ROUNDS_KEY = "com.github.fribourgsdp.radio.GAME_NB_ROUNDS"
const val GAME_HINT_KEY = "com.github.fribourgsdp.radio.GAME_HINT"
const val GAME_PRIVACY_KEY = "com.github.fribourgsdp.radio.GAME_PRIVACY"

class GameSettingsActivity : AppCompatActivity() {

    private lateinit var nameInput : EditText
    private lateinit var nbRoundsInput : EditText
    private lateinit var hintCheckBox : CheckBox
    private lateinit var privacyCheckBox : CheckBox
    private lateinit var startButton : Button

    private lateinit var playlistSearchView : SearchView
    private lateinit var playlistListView : ListView
    private lateinit var playlistsNames : Array<String>
    private lateinit var playlistAdapter : ArrayAdapter<String>

    private var selectedPlaylist = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_settings)

        val host = getHost()

        nameInput = findViewById(R.id.nameInput)
        nbRoundsInput = findViewById(R.id.nbRoundsInput)
        hintCheckBox = findViewById(R.id.hintCheckBox)
        privacyCheckBox = findViewById(R.id.privacyCheckBox)
        startButton = findViewById(R.id.startButton)

        playlistSearchView = findViewById(R.id.playlistSearchView)
        playlistListView = findViewById(R.id.playlistListView)
        playlistsNames = getUserPlaylistNames(host)
        playlistAdapter = ArrayAdapter(
            this, android.R.layout.simple_list_item_1, playlistsNames
        )

        playlistListView.adapter = playlistAdapter

        playlistSearchView.setOnSearchClickListener {
            // When user is clicks on the bar, show the possibilities
            playlistListView.visibility = View.VISIBLE
        }

        playlistSearchView.setOnQueryTextListener(searchViewOnQueryBehavior())

        playlistListView.setOnItemClickListener{ parent, _, position, _ ->
            selectedPlaylist = parent.getItemAtPosition(position).toString()
            playlistSearchView.setQuery(selectedPlaylist, true)
            startButton.isEnabled = true
        }

        startButton.setOnClickListener(
            startButtonBehavior(
                selectedPlaylist,
                host.name,
                nameInput.text.toString().ifEmpty { getString(R.string.default_game_name) },
                nbRoundsInput.text.toString().ifEmpty { getString(R.string.default_game_nb_rounds) }.toInt(),
                hintCheckBox.isChecked,
                privacyCheckBox.isChecked
            )
        )

    }

    private fun getHost() : User {
        // TODO: Update once we can get the user of the phone
        val host = User("The best player")
        host.addPlaylists(setOf(Playlist("Rap Playlist"), Playlist("Country Playlist")))
        return host
    }

    private fun getUserPlaylistNames(user: User) : Array<String> {
        return user.getPlaylists()
            .map { x -> x.name }
            .toTypedArray()
    }

    private fun startButtonBehavior(selectedPlaylist: String, hostName: String, gameName: String, nbRound: Int, withHint: Boolean, private: Boolean) : View.OnClickListener {
        return View.OnClickListener {
            val intent: Intent = Intent(this, LobbyActivity::class.java).apply {
                putExtra(GAME_HOST_KEY, hostName)
                putExtra(GAME_NAME_KEY, gameName)
                putExtra(GAME_PLAYLIST_KEY, selectedPlaylist)
                putExtra(GAME_NB_ROUNDS_KEY, nbRound)
                putExtra(GAME_HINT_KEY, withHint)
                putExtra(GAME_PRIVACY_KEY, private)
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
                    selectedPlaylist = query
                    startButton.isEnabled = true
                } else {
                    Toast.makeText(applicationContext, "Playlist $query not found", Toast.LENGTH_SHORT).show()
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
                return false
            }
        }
    }
}