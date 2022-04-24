package com.github.fribourgsdp.radio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerLobbyAdapter: RecyclerView.Adapter<RecyclerLobbyAdapter.LobbyListViewEntry>() {

    //Each element is a tuple (userId, userName)
    private var userNames: List<Pair<String, String>> = arrayListOf()
    private var micImages = intArrayOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LobbyListViewEntry {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.card_lobby_layout, parent, false)
        return LobbyListViewEntry(inflater)
    }

    override fun onBindViewHolder(holder: LobbyListViewEntry, position: Int) {
        holder.userName.text = userNames[position].second
        holder.micImage.setImageResource(micImages[position])
    }

    override fun getItemCount(): Int {
        return userNames.size
    }

    fun setContent(newUsers: List<Pair<String, String>>, newImages: IntArray) {
        userNames = newUsers
        micImages = newImages
    }
    class LobbyListViewEntry(itemView: View): RecyclerView.ViewHolder(itemView) {
        var micImage: ImageView = itemView.findViewById(R.id.lobbyItemImage)
        var userName: TextView = itemView.findViewById(R.id.lobbyUserName)

    }

}