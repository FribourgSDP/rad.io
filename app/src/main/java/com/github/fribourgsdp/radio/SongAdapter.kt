package com.github.fribourgsdp.radio

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.fribourgsdp.radio.data.Song

class SongAdapter(private val songList: List<Song>, private val listener: OnClickListener) :
    RecyclerView.Adapter<ViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.song_item,
            parent, false)
        return ViewHolder(itemView, listener, itemView.findViewById(R.id.song_title_view), itemView.findViewById(R.id.song_subtitle_view))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val song = songList[position]

        holder.titleView.text = song.name
        holder.subtitleView.text = song.artist
    }

    override fun getItemCount(): Int = songList.size

}