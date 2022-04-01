package com.github.fribourgsdp.radio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SongAdapter(private val songList: List<Song>, private val listener: OnSongClickListener) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.song_item,
            parent, false)
        return SongViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songList[position]

        holder.titleView.text = song.name
        holder.subtitleView.text = song.artist
    }

    override fun getItemCount(): Int = songList.size

    inner class SongViewHolder(itemView: View) :    RecyclerView.ViewHolder(itemView),
                                                    View.OnClickListener{
        val titleView: TextView = itemView.findViewById(R.id.song_title_view)
        val subtitleView: TextView = itemView.findViewById(R.id.song_subtitle_view)

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

    interface OnSongClickListener {
        fun onItemClick(position: Int)
    }
}