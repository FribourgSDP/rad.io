package com.github.fribourgsdp.radio

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val GAME_UID_KEY = "com.github.fribourgsdp.radio.GAME_UID"
const val GAME_HOST_NAME_KEY = "com.github.fribourgsdp.radio.GAME_HOST_NAME"
const val GAME_PLAYLIST_NAME_KEY = "com.github.fribourgsdp.radio.GAME_PLAYLIST_NAME"


open class JoinGameActivity : MyAppCompatActivity() {
    private val db = this.initDatabase()

    private lateinit var idInput: EditText
    private lateinit var joinButton : Button
    private lateinit var joinErrorView : TextView
    private lateinit var joinWithQRCodeButton : Button
    private lateinit var qrCodeScan: DialogFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_game)

        idInput = findViewById(R.id.gameToJoinID)
        joinButton = findViewById(R.id.joinGameButton)
        joinWithQRCodeButton = findViewById(R.id.joinWithQRCode)
        joinErrorView = findViewById(R.id.joinErrorView)

        idInput.addTextChangedListener {
            joinButton.isEnabled = idInput.text.toString().trim().isNotEmpty()
        }

        joinButton.setOnClickListener {
            connectToLobby(idInput.text.toString().trim().toLong())
        }

        // trigger the button when the user presses "enter" in the text field
        idInput.setOnEditorActionListener { _: TextView?, actionId: Int, _: KeyEvent? ->
            if (actionId == EditorInfo.IME_ACTION_DONE && joinButton.isEnabled) {
                joinButton.callOnClick()
                return@setOnEditorActionListener true
            }
            false
        }
        initJoinWithQRCode()
    }

    open fun initDatabase(): Database {
        return FirestoreDatabase()
    }

    private fun initJoinWithQRCode(){
        qrCodeScan = JoinWithQRCodeFragment(this, this)
        joinWithQRCodeButton.setOnClickListener{
            qrCodeScan.show(supportFragmentManager, "qrCodeForJoiningGame")
            supportFragmentManager
                .setFragmentResultListener("idRequest", this) { _, bundle ->
                    val id = bundle.getLong("id")
                    connectToLobby(id)
                }
        }
    }

    private fun connectToLobby(id: Long) {
        db.getGameSettingsFromLobby(id).addOnSuccessListener { settings ->
            db.addUserToLobby(id, User.load(this), (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)).addOnSuccessListener {
                startActivity(Intent(this, LobbyActivity::class.java).apply {
                    putExtra(GAME_HOST_NAME_KEY, settings.hostName)
                    putExtra(GAME_NAME_KEY, settings.name)
                    putExtra(GAME_PLAYLIST_NAME_KEY, settings.playlistName)
                    putExtra(GAME_NB_ROUNDS_KEY, settings.nbRounds)
                    putExtra(GAME_HINT_KEY, settings.withHint)
                    putExtra(GAME_PRIVACY_KEY, settings.isPrivate)
                    putExtra(GAME_IS_HOST_KEY, false)
                    putExtra(GAME_UID_KEY, id)
                })

            }.addOnFailureListener {
                joinErrorView.text = getString(R.string.join_fail_format, id)
                joinErrorView.visibility = View.VISIBLE
            }

        }.addOnFailureListener {
            joinErrorView.text = getString(R.string.lobby_not_found, id)
            joinErrorView.visibility = View.VISIBLE
        }
    }
}