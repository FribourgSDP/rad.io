package com.github.fribourgsdp.radio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import com.google.android.gms.tasks.Task

const val GAME_UID_KEY = "com.github.fribourgsdp.radio.GAME_UID"


class JoinGameActivity : AppCompatActivity() {
    private val db = Database()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_game)

        val idInput: EditText = findViewById(R.id.gameToJoinID)
        val joinButton : Button = findViewById(R.id.joinGameButton)

        idInput.addTextChangedListener {
            joinButton.isEnabled = idInput.text.toString().trim().isNotEmpty()
        }

        joinButton.setOnClickListener {
            connectToLobby(idInput.text.toString().trim().toLong())
        }

        // trigger the button when the user presses "enter" in the text field
        idInput.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                joinButton.callOnClick()
                return@setOnEditorActionListener true
            }
            false
        }

    }

    fun addUserToLobby(id: Long): Task<Void> {
        // TODO: Get the user using the phone
        return db.addUserToLobby(id, User("Archibald"))
    }

    private fun connectToLobby(id: Long) {
        db.getGameSettingsFromLobby(id).addOnSuccessListener { settings ->
            addUserToLobby(id).addOnSuccessListener {

                startActivity(Intent(this, LobbyActivity::class.java).apply {
                    putExtra(GAME_HOST_KEY, settings.host.name)
                    putExtra(GAME_NAME_KEY, settings.name)
                    putExtra(GAME_PLAYLIST_KEY, settings.playlist.name)
                    putExtra(GAME_NB_ROUNDS_KEY, settings.nbRounds)
                    putExtra(GAME_HINT_KEY, settings.withHint)
                    putExtra(GAME_PRIVACY_KEY, settings.isPrivate)
                    putExtra(GAME_IS_HOST_KEY, false)
                    putExtra(GAME_UID_KEY, id)
                })

            }.addOnFailureListener {
                Toast.makeText(this, "Failed to join lobby $id.", Toast.LENGTH_LONG).show()
            }

        }.addOnFailureListener {
            Toast.makeText(this, "Lobby $id not found.", Toast.LENGTH_LONG).show()
        }
    }
}