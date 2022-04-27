package com.github.fribourgsdp.radio

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PlaylistsFragmentHolderActivity : MyAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlists_fragment_holder)

        //initialise songs recycler view fragment
        val bundle = Bundle()
        bundle.putString(PLAYLIST_DATA, intent.getStringExtra(PLAYLIST_DATA))
        MyFragment.beginTransaction<PlaylistSongsFragment>(supportFragmentManager, bundle)
    }
}