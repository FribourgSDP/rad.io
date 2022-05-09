package com.github.fribourgsdp.radio.util

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ViewHolder(itemView: View, private val listener: OnClickListener, val titleView: TextView, val subtitleView: TextView) : RecyclerView.ViewHolder(itemView), View.OnClickListener{

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
