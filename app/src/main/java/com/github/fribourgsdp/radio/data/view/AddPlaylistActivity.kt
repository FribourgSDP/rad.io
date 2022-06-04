package com.github.fribourgsdp.radio.data.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.gms.tasks.Tasks
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.fribourgsdp.radio.config.MyAppCompatActivity
import com.github.fribourgsdp.radio.util.OnClickListener
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.data.Genre
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.external.musixmatch.LyricsGetter
import com.github.fribourgsdp.radio.external.musixmatch.MusixmatchLyricsGetter
import com.google.android.gms.tasks.Task
import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.database.DatabaseHolder
import com.github.fribourgsdp.radio.database.FirestoreDatabase
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

open class AddPlaylistActivity : MyAppCompatActivity(), SavePlaylistOnlinePickerDialog.OnPickListener,
                                                                                    DatabaseHolder {

    private val listSongs = ArrayList<Song>()
    private var listNames = ArrayList<String>()
    private lateinit var listAdapter: SongAdapter
    private lateinit var recyclerView : RecyclerView
    private lateinit var errorTextView : TextView
    private lateinit var confirmButton : Button
    private lateinit var genreSpinner: Spinner
    private lateinit var generateLyricsCheckBox : CheckBox
    lateinit var user : User
    private var db : Database = initializeDatabase()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_playlist)
        initViews()
        genreSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Genre.values())

        User.loadOrDefault(this).addOnSuccessListener { u ->
            user = u
        }

        processIntent(intent)

        listAdapter = SongAdapter(listSongs, object : OnClickListener {
            override fun onItemClick(position: Int) {
            }
        })

        val itemTouchHelper = ItemTouchHelper(SongSwipeHelper(recyclerView, listSongs,
            findViewById(R.id.AddPlaylistRootView), this))
        recyclerView.adapter = listAdapter
        itemTouchHelper.attachToRecyclerView(recyclerView)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        errorTextView = findViewById(R.id.addPlaylistErrorTextView)

        if(!hasConnectivity(this)){
            generateLyricsCheckBox.isEnabled = false
        }

    }

    private fun initViews(){
        generateLyricsCheckBox = findViewById(R.id.generateLyricsCheckBox)
        genreSpinner = findViewById(R.id.genreSpinner)
        errorTextView = findViewById(R.id.addPlaylistErrorTextView)
        confirmButton = findViewById(R.id.confirmBtn)
        recyclerView = findViewById(R.id.list_playlist_creation)
    }

    /**
     * Function called directly by the XML file activity_add_playlist when the button to add song is pressed.
     * Adds a new song with the given parameters to the temporary playlist.
     */
    fun addItems(view : View) {
        val songNameTextView : EditText = findViewById(R.id.addSongToPlaylistSongName)
        val artistNameTextView : EditText = findViewById(R.id.addSongToPlaylistArtistName)
        val songName : String = songNameTextView.text.toString()
        val artistName : String = artistNameTextView.text.toString()

        if (songName.isEmpty()){
            errorTextView.text = this.applicationContext.resources.getText(R.string.name_cannot_be_empty)
        } else {
            listSongs.add(Song(songName, artistName))
            listNames.add(0, "$artistName : $songName")
            errorTextView.text = ""
            songNameTextView.setText("")
            artistNameTextView.setText("")
            listAdapter.notifyItemInserted(listSongs.size-1)
        }
    }
    /**
     * Function called directly by the XML file activity_add_playlist when the button to create playlist is pressed.
     * Adds the temporary playlist to the user and returns to activity UserProfileActivity
     */
    fun createPlaylist(view : View) {
        val playlistName : String = findViewById<EditText>(R.id.newPlaylistName).text.toString()

        when {
            playlistName.isEmpty() -> {
                displayError(R.string.playlist_has_no_name)
            }
            listSongs.isEmpty() -> {
                displayError(R.string.playlist_is_empty)
            }
            else -> {
                if(!hasConnectivity(this) || !user.isGoogleUser){
                    savePlaylistLocally()
                }else{
                    val savePlaylistOnlinePicker = SavePlaylistOnlinePickerDialog(this)
                    savePlaylistOnlinePicker.show(supportFragmentManager, "SavePlaylistOnlinePicker")
                }

            }
        }
    }

    /**
     * Fills error text view
     */
    private fun displayError(id : Int){
        errorTextView.text = this.applicationContext.resources.getText(id)
    }
    private fun processIntent(intent: Intent){
        if (intent.hasExtra(PLAYLIST_TO_MODIFY)){
            val serializedPlaylist = intent.getStringExtra(PLAYLIST_TO_MODIFY)
            val playlist : Playlist = Json.decodeFromString(serializedPlaylist!!)
            listSongs.addAll(playlist.getSongs())
            findViewById<EditText>(R.id.newPlaylistName).setText(playlist.name)
        }
        if(intent.getBooleanExtra(ADD_PLAYLIST_FLAG, true)){
            confirmButton.text = getString(R.string.create_playlist)
        } else{
            confirmButton.text = getString(R.string.update_playlist)

        }
    }


    private fun savePlaylistLocally(){
        if(generateLyricsCheckBox.isChecked){
            Toast.makeText(this,getString(R.string.no_lyrics_generation_offline),Toast.LENGTH_SHORT).show()
        }
        val playlistName : String = findViewById<EditText>(R.id.newPlaylistName).text.toString()
        val genre : Genre = genreSpinner.selectedItem as Genre
        val playlist = Playlist(playlistName,listSongs.toSet(),genre)
        user.addPlaylist(playlist)
        user.save(this)
        val intent = Intent(this@AddPlaylistActivity, UserProfileActivity::class.java)
        startActivity(intent)
    }
    override fun onPick(online: Boolean) {
        val playlistName: String = findViewById<EditText>(R.id.newPlaylistName).text.toString()
        val genre: Genre = genreSpinner.selectedItem as Genre
        Toast.makeText(this, R.string.creating_playlist_toast, Toast.LENGTH_LONG).show()
        val playlist = Playlist(playlistName, listSongs.toSet(), genre)
        val t = if (generateLyricsCheckBox.isChecked) {
            playlist.loadLyrics()
            //loadLyrics(playlist)
        } else {
            Tasks.forResult(null)
        }
        t.continueWith {
            //playlist = it.result
            val playlistTask = if (online) playlist.transformToOnline().addOnSuccessListener {
                playlist.saveOnline()
            } else Tasks.forResult(playlist)

            playlistTask.addOnSuccessListener {
                user.addPlaylist(playlist)
                user.save(this)
                user.onlineCopyAndSave()
                val intent = Intent(this@AddPlaylistActivity, UserProfileActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun allFieldsEmpty(): Boolean {
        if (findViewById<EditText>(R.id.newPlaylistName).text.toString().isNotBlank()){
            return false
        }
        if (genreSpinner.selectedItem.toString() != Genre.values()[0].toString()){
            return false
        }
        if (findViewById<EditText>(R.id.addSongToPlaylistSongName).text.toString().isNotBlank()){
            return false
        }
        if (findViewById<EditText>(R.id.addSongToPlaylistArtistName).text.toString().isNotBlank()){
            return false
        }
        if (listSongs.isNotEmpty()){
            return false
        }
        return true
    }

    override fun onBackPressed() {
        if (allFieldsEmpty()){
            super.onBackPressed()
            val intent = Intent(this, UserProfileActivity::class.java)
            startActivity(intent)
            finish()
        }
        else {
            val warningDialog = QuitAddPlaylistDialog(this)
            warningDialog.show(supportFragmentManager, "warningForQuittingAddPlaylist")

        }
    }
}