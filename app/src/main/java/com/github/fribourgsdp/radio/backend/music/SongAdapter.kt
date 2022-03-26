package com.github.fribourgsdp.radio.backend.music

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.fribourgsdp.radio.R

class SongAdapter(private val songList: List<Song>) :
    RecyclerView.Adapter<SongAdapter.SongViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.song_item,
            parent, false)
        return SongViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: SongViewHolder, position: Int) {
        val song = songList[position]

        holder.titleView.text = song.name
        holder.subtitleView.text = song.artist
    }

    override fun getItemCount(): Int = songList.size

    inner class SongViewHolder(itemView: View) :    RecyclerView.ViewHolder(itemView){
        val titleView: TextView = itemView.findViewById(R.id.song_title_view)
        val subtitleView: TextView = itemView.findViewById(R.id.song_subtitle_view)
    }
}