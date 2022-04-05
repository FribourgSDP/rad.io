package com.github.fribourgsdp.radio

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val PLAYLIST_DATA = "com.github.fribourgsdp.radio.PLAYLIST_INNER_DATA"

class UserPlaylistsFragment : MyFragment(R.layout.fragment_user_playlists_display), OnClickListener{
    private lateinit var user: User
    private lateinit var userPlaylists: List<Playlist>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let { args ->
            args.getString(USER_DATA).let { serializedUser ->
                user = Json.decodeFromString(serializedUser!!)
                userPlaylists = user.getPlaylists().toList()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeRecyclerView()
    }

    private fun initializeRecyclerView() {
        val songDisplay : RecyclerView = requireView().findViewById(R.id.playlist_recycler_view)
        songDisplay.adapter = PlaylistAdapter(user.getPlaylists().toList(), this)
        songDisplay.layoutManager = (LinearLayoutManager(activity))
        songDisplay.setHasFixedSize(true)
    }

    override fun onItemClick(position: Int) {
        activity?.run {
            val intent = Intent(this, PlaylistsFragmentHolderActivity::class.java)
                .putExtra(PLAYLIST_DATA, Json.encodeToString(userPlaylists[position]))
            startActivity(intent)
        }
    }
}