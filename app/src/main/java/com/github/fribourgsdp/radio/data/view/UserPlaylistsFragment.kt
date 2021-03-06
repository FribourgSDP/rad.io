package com.github.fribourgsdp.radio.data.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.config.ConnectivityChecker
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.User
import com.github.fribourgsdp.radio.util.MyFragment
import com.github.fribourgsdp.radio.util.OnClickListener

const val PLAYLIST_DATA = "com.github.fribourgsdp.radio.PLAYLIST_INNER_DATA"

class UserPlaylistsFragment : MyFragment(R.layout.fragment_user_playlists_display),
    OnClickListener,ConnectivityChecker {
    private lateinit var user: User
    private lateinit var userPlaylists: List<Playlist>
    private lateinit var songDisplay: RecyclerView
    private lateinit var startForResult : ActivityResultLauncher<Intent>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadData()
        initializeRecyclerView()
        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                _: ActivityResult -> notifyUserChanged()
        }
    }

    private fun initializeRecyclerView() {
        songDisplay = requireView().findViewById(R.id.playlist_recycler_view)
        songDisplay.adapter = PlaylistAdapter(userPlaylists, this)
        songDisplay.layoutManager = (LinearLayoutManager(activity))
        songDisplay.setHasFixedSize(true)
    }

    override fun onItemClick(position: Int) {
        activity?.run {
            if(!hasConnectivity(requireContext()) && userPlaylists[position].savedOnline){
                Toast.makeText(this,getString(R.string.offline_error_message_toast), Toast.LENGTH_SHORT).show()
            }else{
                val intent = Intent(this, PlaylistsFragmentHolderActivity::class.java)
                    .putExtra(PLAYLIST_DATA, userPlaylists[position].name)
                startForResult.launch(intent)
            }

        }
    }

    fun notifyUserChanged() {
        loadData()
        // for some reason, notifying changes does not work
        // but creating a new adapter does
        songDisplay.adapter = PlaylistAdapter(userPlaylists, this)
    }

    private fun loadData(){
        user = User.load(requireContext())
        userPlaylists = user.getPlaylists().toMutableList()
    }
}