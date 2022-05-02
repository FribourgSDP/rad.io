package com.github.fribourgsdp.radio

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class PublicLobbiesAdapter(private val context: Context, private val db: Database = FirestoreDatabase()): RecyclerView.Adapter<PublicLobbiesAdapter.ViewHolder>() {
    private var lobbies: List<LobbyData>? = db.getPublicLobbies().result

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // init update listener
        db.listenToPublicLobbiesUpdate { value, _ ->
            lobbies = value

            // update the whole list
            notifyDataSetChanged()
        }

        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_lobby, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        lobbies?.let { holder.updateItems(it[position]) }
    }

    override fun getItemCount(): Int {
        return lobbies?.size ?: 0
    }

    // TODO: On click

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val itemId:  TextView = itemView.findViewById(R.id.lobbyIdTextView)
        private val itemName:  TextView = itemView.findViewById(R.id.lobbyNameTextView)
        private val itemHostName: TextView = itemView.findViewById(R.id.lobbyHostNameTextView)

        internal fun updateItems(lobby: LobbyData) {
            itemId.text = lobby.id.toString()
            itemName.text = context.getString(R.string.game_name_format, lobby.name)
            itemHostName.text = context.getString(R.string.host_name_format, lobby.hostName)
        }
    }
}