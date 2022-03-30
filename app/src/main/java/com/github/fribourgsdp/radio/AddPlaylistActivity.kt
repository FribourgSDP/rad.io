package com.github.fribourgsdp.radio

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class AddPlaylistActivity : AppCompatActivity() {
    private val listSongs = ArrayList<Song>()
    private var listNames = ArrayList<String>()
    lateinit var listAdapter: ArrayAdapter<String>
    lateinit var listView : ListView
    lateinit var errorTextView : TextView
    lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_playlist)
        listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listNames)
        listView = findViewById(android.R.id.list)
        listView.adapter = listAdapter
        errorTextView = findViewById(R.id.addPlaylistErrorTextView)

        user = UserProfileActivity.loadUser(this)
    }
    fun addItems(view : View) : Unit {
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
            listAdapter.notifyDataSetChanged()
        }
    }
    fun createPlaylist(view : View) : Unit {
        val playlistName : String = findViewById<EditText>(R.id.newPlaylistName).text.toString()
        if (playlistName.isEmpty()){
            errorTextView.text = this.applicationContext.resources.getText(R.string.playlist_has_no_name)
        } else if (listSongs.isEmpty()){
            errorTextView.text = this.applicationContext.resources.getText(R.string.playlist_is_empty)
        } else {
            val intent = Intent(this@AddPlaylistActivity, UserProfileActivity::class.java)
            val playlist = Playlist(playlistName, listSongs.toSet(), Genre.NONE)
            // TODO: Add feature to select genre
            user.addPlaylist(playlist)
            user.save(this)
            startActivity(intent)
        }
    }
}