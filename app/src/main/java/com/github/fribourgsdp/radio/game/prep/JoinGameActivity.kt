package com.github.fribourgsdp.radio.game.prep

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.fribourgsdp.radio.data.LobbyDataKeys
import com.github.fribourgsdp.radio.game.view.PublicLobbiesAdapter
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.config.MyAppCompatActivity
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.database.FirestoreDatabase

const val GAME_UID_KEY = "com.github.fribourgsdp.radio.GAME_UID"
const val GAME_HOST_NAME_KEY = "com.github.fribourgsdp.radio.GAME_HOST_NAME"
const val GAME_PLAYLIST_NAME_KEY = "com.github.fribourgsdp.radio.GAME_PLAYLIST_NAME"


open class JoinGameActivity : MyAppCompatActivity() {
    private val db = this.initDatabase()

    private lateinit var idInput: EditText
    private lateinit var joinButton : Button
    private lateinit var joinErrorView : TextView
    private lateinit var lobbiesRecyclerView: RecyclerView
    private lateinit var spinner: Spinner
    private lateinit var joinWithQRCodeButton : Button
    private lateinit var qrCodeScan: DialogFragment


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_join_game)

        initViews()
        initPublicLobbiesDisplay()
        initJoinWithQRCode()

    }

    private fun initViews() {
        idInput = findViewById(R.id.gameToJoinID)
        joinButton = findViewById(R.id.joinGameButton)
        joinWithQRCodeButton = findViewById(R.id.joinWithQRCode)
        joinErrorView = findViewById(R.id.joinErrorView)
        lobbiesRecyclerView = findViewById(R.id.publicLobbiesRecyclerView)
        spinner = findViewById(R.id.lobbySortSpinner)

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
    }

    open fun initDatabase(): Database {
        return FirestoreDatabase()
    }

    protected open fun createQRCodeFragment() : DialogFragment{
        return JoinWithQRCodeFragment(this, this)
    }

    private fun initJoinWithQRCode(){
        qrCodeScan = createQRCodeFragment()
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
                    putExtra(GAME_DURATION_KEY, settings.singerDuration)
                    putExtra(GAME_UID_KEY, id)
                    putExtra(GAME_IS_NO_SING_MODE, settings.noSing)
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

    private fun initPublicLobbiesDisplay() {
        // init the cards
        lobbiesRecyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = PublicLobbiesAdapter(this, db).apply {
            setOnPickListener { connectToLobby(it) }
        }
        lobbiesRecyclerView.adapter = adapter

        // set the context for LobbyDataKeys to use the values in string.xml
        LobbyDataKeys.setContext(this)

        // init the spinner
        spinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, LobbyDataKeys.values()).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        spinner.onItemSelectedListener = LobbySelector(adapter)
    }

    private class LobbySelector(private val publicLobbiesAdapter: PublicLobbiesAdapter): AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val key = parent?.getItemAtPosition(position) as LobbyDataKeys?
            if (key != null) {
                publicLobbiesAdapter.sortBy(key)
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>?) {
            // Do nothing
        }

    }
}