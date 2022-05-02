package com.github.fribourgsdp.radio

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class PublicLobbiesAdapter(private val context: Context, private val db: Database = FirestoreDatabase()): RecyclerView.Adapter<PublicLobbiesAdapter.ViewHolder>() {
    private var lobbies: List<LobbyData>? = null
    private var listener: OnPickListener? = null

    init {
        db.getPublicLobbies().addOnSuccessListener {
            lobbies = it

            // update the whole list
            notifyDataSetChanged()
        }.addOnFailureListener {
            lobbies = null
        }

        // init update listener
        db.listenToPublicLobbiesUpdate { value, _ ->
            lobbies = value

            // update the whole list
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_lobby, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        lobbies?.let { holder.updateItems(it[position]) }
    }

    override fun getItemCount(): Int {
        return lobbies?.size ?: 0
    }

    fun sortBy(key: LobbyDataKeys) {
        when (key) {
            LobbyDataKeys.ID -> lobbies?.sortedBy { it.id }
            LobbyDataKeys.NAME -> lobbies?.sortedBy { it.name }
            LobbyDataKeys.HOSTNAME -> lobbies?.sortedBy { it.hostName }
        }

        if (lobbies != null) {
            notifyDataSetChanged()
        }
    }

    fun setOnPickListener(l: OnPickListener) {
        listener = l
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val itemId:  TextView = itemView.findViewById(R.id.lobbyIdTextView)
        private val itemName:  TextView = itemView.findViewById(R.id.lobbyNameTextView)
        private val itemHostName: TextView = itemView.findViewById(R.id.lobbyHostNameTextView)
        private val itemCardView: CardView = itemView.findViewById(R.id.card_lobby)

        internal fun updateItems(lobby: LobbyData) {
            itemId.text = lobby.id.toString()
            itemName.text = context.getString(R.string.game_name_format, lobby.name)
            itemHostName.text = context.getString(R.string.host_name_format, lobby.hostName)
            itemCardView.setOnClickListener { listener?.onPick(lobby.id) }
        }
    }

    public fun interface OnPickListener {
        fun onPick(lobbyId: Long)
    }
}