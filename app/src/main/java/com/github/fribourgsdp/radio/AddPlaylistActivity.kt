package com.github.fribourgsdp.radio

import android.app.PendingIntent.getActivity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class AddPlaylistActivity : AppCompatActivity() {
    private val listSongs = ArrayList<Song>()
    private var listNames = ArrayList<String>()
    private lateinit var listAdapter: SongAdapter
    private lateinit var recyclerView : RecyclerView
    private lateinit var errorTextView : TextView
    lateinit var user : User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_playlist)

//        listAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listNames)
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

        user = UserProfileActivity.loadUser(this)
    }
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
                val intent = Intent(this@AddPlaylistActivity, UserProfileActivity::class.java)
                val playlist = Playlist(playlistName, listSongs.toSet(), Genre.NONE)
                // TODO: Add feature to select genre
                user.addPlaylist(playlist)
                user.save(this)
                startActivity(intent)
            }
        }
    }
    private fun displayError(id : Int){
        errorTextView.text = this.applicationContext.resources.getText(id)
    }
}