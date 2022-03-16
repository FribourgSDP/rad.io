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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_settings)

        val host = getHost()

        val nameInput : EditText = findViewById(R.id.nameInput)
        val nbRoundsInput : EditText = findViewById(R.id.nbRoundsInput)
        val hintCheckBox : CheckBox = findViewById(R.id.hintCheckBox)
        val privacyCheckBox : CheckBox = findViewById(R.id.privacyCheckBox)
        val startButton : Button = findViewById(R.id.startButton)

        val playlistSearchView : SearchView = findViewById(R.id.playlistSearchView)
        val playlistListView : ListView = findViewById(R.id.playlistListView)
        val playlistsNames = getUserPlaylistNames(host)
        val playlistAdapter : ArrayAdapter<String> = ArrayAdapter(
            this, android.R.layout.simple_list_item_1, playlistsNames
        )
        playlistListView.adapter = playlistAdapter

        playlistSearchView.setOnSearchClickListener {
            // When user is clicks on the bar, show the possibilities
            playlistListView.visibility = View.VISIBLE
        }

        playlistSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                playlistSearchView.clearFocus()

                // When selected, hide the possibilities
                playlistListView.visibility = View.GONE

                if (playlistsNames.contains(query)) {
                    playlistAdapter.filter.filter(query)
                } else {
                    Toast.makeText(applicationContext, "Playlist $query not found", Toast.LENGTH_SHORT).show()
                }

                return false

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // When user is typing, show the possibilities
                playlistListView.visibility = View.VISIBLE
                playlistAdapter.filter.filter(newText)
                return false
            }
        })

        var selectedPlaylist = ""
        playlistListView.setOnItemClickListener{ parent, _, position, _ ->
            selectedPlaylist = parent.getItemAtPosition(position).toString()
            playlistSearchView.setQuery(selectedPlaylist, true)
        }

        startButton.setOnClickListener {
            if (selectedPlaylist.isEmpty()) {
                Toast.makeText(applicationContext, "You need to select a valid playlist.", Toast.LENGTH_SHORT).show()
            } else {
                val intent : Intent = Intent(this, LobbyActivity::class.java).apply {
                    putExtra(GAME_HOST_KEY, host.name)
                    putExtra(GAME_NAME_KEY,
                        nameInput.text.toString().ifEmpty { getString(R.string.default_game_name) }
                    )
                    putExtra(GAME_PLAYLIST_KEY, selectedPlaylist)
                    putExtra(GAME_NB_ROUNDS_KEY,
                        nbRoundsInput.text.toString().ifEmpty { getString(R.string.default_game_nb_rounds) }.toInt()
                    )
                    putExtra(GAME_HINT_KEY, hintCheckBox.isChecked)
                    putExtra(GAME_PRIVACY_KEY, privacyCheckBox.isChecked)
                }
                startActivity(intent)
            }
        }

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
}