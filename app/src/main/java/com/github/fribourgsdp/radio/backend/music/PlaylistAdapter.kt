package com.github.fribourgsdp.radio.backend.music

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.fribourgsdp.radio.R

class PlaylistAdapter(private val playlistList: List<Playlist>,
                      private val listener: OnPlaylistClickListener
) :
    RecyclerView.Adapter<PlaylistAdapter.PlaylistViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.playlist_item,
            parent, false)
        return PlaylistViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlistList[position]

        holder.titleView.text = playlist.name
        holder.subtitleView.text = playlist.genre.toString()
    }

    override fun getItemCount(): Int = playlistList.size

    inner class PlaylistViewHolder(itemView: View) :    RecyclerView.ViewHolder(itemView),
                                                        View.OnClickListener {
        val titleView: TextView = itemView.findViewById(R.id.playlist_title_view)
        val subtitleView: TextView = itemView.findViewById(R.id.playlist_subtitle_view)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val position = absoluteAdapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnPlaylistClickListener {
        fun onItemClick(position: Int)
    }
}