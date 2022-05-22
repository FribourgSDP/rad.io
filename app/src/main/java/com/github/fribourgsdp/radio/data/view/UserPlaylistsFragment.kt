package com.github.fribourgsdp.radio.data.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.fribourgsdp.radio.util.MyFragment
import com.github.fribourgsdp.radio.util.OnClickListener
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.User
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

const val PLAYLIST_DATA = "com.github.fribourgsdp.radio.PLAYLIST_INNER_DATA"

class UserPlaylistsFragment : MyFragment(R.layout.fragment_user_playlists_display),
    OnClickListener {
    private lateinit var user: User
    private lateinit var userPlaylists: MutableList<Playlist>
    private lateinit var songDisplay: RecyclerView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
        initializeRecyclerView()
    }

    private fun initializeRecyclerView() {
        songDisplay = requireView().findViewById(R.id.playlist_recycler_view)
        songDisplay.adapter = PlaylistAdapter(userPlaylists, this)
        songDisplay.layoutManager = (LinearLayoutManager(activity))
        songDisplay.setHasFixedSize(true)
    }

    override fun onItemClick(position: Int) {
        activity?.run {
            val intent = Intent(this, PlaylistsFragmentHolderActivity::class.java)
                .putExtra(PLAYLIST_DATA, userPlaylists[position].name)
            startActivity(intent)
        }
    }

    fun notifyUserChanged() {
        loadData()
        songDisplay.adapter?.notifyDataSetChanged()
    }

    private fun loadData(){
        user = User.load(requireContext())
        userPlaylists = user.getPlaylists().toMutableList()
    }

    override fun onResume() {
        notifyUserChanged()
        super.onResume()
    }
}