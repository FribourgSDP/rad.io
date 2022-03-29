package com.github.fribourgsdp.radio

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

const val COMING_FROM_ADD_PLAYLIST_ACTIVITY_FLAG : String = "com.github.fribourgsdp.radio.addPlaylistManually"

class AddPlaylistActivity : AppCompatActivity() {
    private val listSongs = ArrayList<Song>()
    private var listNames = ArrayList<String>()
    lateinit var listAdapter: ArrayAdapter<String>
    lateinit var listView : ListView
    lateinit var errorTextView : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_playlist)
        listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listNames)
        listView = findViewById(android.R.id.list)
        listView.adapter = listAdapter
        errorTextView = findViewById(R.id.errorTextView)
    }
    fun addItems(view : View) : Unit {
        val songName : String = findViewById<EditText>(R.id.songName).text.toString()
        val artistName : String = findViewById<EditText>(R.id.artistName).text.toString()

        if (songName.isEmpty()){
            errorTextView.text = this.applicationContext.resources.getText(R.string.name_cannot_be_empty)
        } else {
            listSongs.add(Song(songName, artistName))
            listNames.add(0, "$artistName : $songName")
            errorTextView.text = ""
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
            val playlist = HashSet<Playlist>()
            // TODO: Add feature to select genre
            playlist.add(Playlist(playlistName, listSongs.toSet(), Genre.NONE))
            intent.putExtra("playlists", playlist)
            intent.putExtra(COMING_FROM_ADD_PLAYLIST_ACTIVITY_FLAG, false)
            startActivity(intent)
        }
    }
}