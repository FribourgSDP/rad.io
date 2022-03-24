package com.github.fribourgsdp.radio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PlaylistAdapter(private val playlistList: List<Playlist>) :
    RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.playlist_item,
            parent, false)
        return PlaylistViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlistList[position]

        holder.titleView.text = playlist.name
        holder.subtitleView.text = playlist.genre.toString()
    }

    override fun getItemCount(): Int = playlistList.size

    class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView = itemView.findViewById(R.id.playlist_title_view)
        val subtitleView: TextView = itemView.findViewById(R.id.playlist_subtitle_view)
    }
}