package com.github.fribourgsdp.radio.data.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.fribourgsdp.radio.OnClickListener
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.ViewHolder
import com.github.fribourgsdp.radio.data.Playlist

class PlaylistAdapter(private val playlistList: List<Playlist>,
                      private val listener: OnClickListener
) :
    RecyclerView.Adapter<ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.playlist_item,
            parent, false)
        return ViewHolder(itemView, listener, itemView.findViewById(R.id.playlist_title_view), itemView.findViewById(
            R.id.playlist_subtitle_view
        ))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val playlist = playlistList[position]

        holder.titleView.text = playlist.name
        holder.subtitleView.text = playlist.genre.toText()
    }

    override fun getItemCount(): Int = playlistList.size

}