package com.github.fribourgsdp.radio

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class PlaylistsFragmentHolderActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_playlists_fragment_holder)

        //initialise songs recycler view fragment
        val bundle = Bundle()
        bundle.putString(PLAYLIST_DATA, intent.getStringExtra(PLAYLIST_DATA))
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, PlaylistSongsFragment::class.java, bundle)
            .commit()
    }
}