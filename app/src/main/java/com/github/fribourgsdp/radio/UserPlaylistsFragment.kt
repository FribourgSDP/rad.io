package com.github.fribourgsdp.radio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

const val PLAYLIST_DATA = "com.github.fribourgsdp.radio.PLAYLIST_INNER_DATA"

class UserPlaylistsFragment : Fragment(), PlaylistAdapter.OnPlaylistClickListener{
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

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_user_playlists_display, container, false)
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
            val bundle = Bundle()
            bundle.putString(PLAYLIST_DATA, Json.encodeToString(userPlaylists[position]))
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, PlaylistSongsFragment::class.java, bundle)
                .addToBackStack("PlaylistDisplayFragment")
                .commit()
        }
    }
}