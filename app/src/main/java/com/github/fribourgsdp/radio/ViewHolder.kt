package com.github.fribourgsdp.radio

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ViewHolder(itemView: View, private val listener: OnClickListener) :    RecyclerView.ViewHolder(itemView),
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

interface OnClickListener{
    fun onItemClick(position: Int)
}
