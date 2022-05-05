package com.github.fribourgsdp.radio.data.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.google.android.gms.tasks.Tasks
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.fribourgsdp.radio.MyAppCompatActivity
import com.github.fribourgsdp.radio.OnClickListener
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.data.Genre
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.User
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class AddPlaylistActivity : MyAppCompatActivity(), SavePlaylistOnlinePickerDialog.OnPickListener {

    private val listSongs = ArrayList<Song>()
    private var listNames = ArrayList<String>()
    private lateinit var listAdapter: SongAdapter
    private lateinit var recyclerView : RecyclerView
    private lateinit var errorTextView : TextView
    private lateinit var genreSpinner: Spinner
    lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_playlist)

        genreSpinner = findViewById(R.id.genreSpinner)
        genreSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Genre.values())
        errorTextView = findViewById(R.id.addPlaylistErrorTextView)

        User.loadOrDefault(this).addOnSuccessListener { u ->
            user = u
        }


        processIntent(intent)

        recyclerView = findViewById(R.id.list_playlist_creation)
        listAdapter = SongAdapter(listSongs, object : OnClickListener{
            override fun onItemClick(position: Int) {
                listSongs.removeAt(position)
                listAdapter.notifyItemRemoved(position)
            }
        })

        recyclerView.adapter = listAdapter
        val layoutManager = LinearLayoutManager(this)
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        errorTextView = findViewById(R.id.addPlaylistErrorTextView)

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
                val savePlaylistOnlinePicker = SavePlaylistOnlinePickerDialog(this)
                savePlaylistOnlinePicker.show(supportFragmentManager, "SavePlaylistOnlinePicker")
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
    }


    override fun onPick(online: Boolean) {
        val playlistName : String = findViewById<EditText>(R.id.newPlaylistName).text.toString()
        val genre : Genre = genreSpinner.selectedItem as Genre
        val playlist = Playlist(playlistName,listSongs.toSet(),genre)
        val playlistTask = if (online) playlist.transformToOnline().addOnSuccessListener {
            playlist.saveOnline()
        } else Tasks.forResult(playlist)


        playlistTask.addOnSuccessListener {
            user.addPlaylist(playlist)
            user.save(this)
            val intent = Intent(this@AddPlaylistActivity, UserProfileActivity::class.java)
            startActivity(intent)
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