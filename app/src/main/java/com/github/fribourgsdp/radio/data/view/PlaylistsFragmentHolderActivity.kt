package com.github.fribourgsdp.radio.data.view

import android.os.Bundle
import com.github.fribourgsdp.radio.config.MyAppCompatActivity
import com.github.fribourgsdp.radio.util.MyFragment
import com.github.fribourgsdp.radio.R

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