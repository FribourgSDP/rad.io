package com.github.fribourgsdp.radio.game.view

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.github.fribourgsdp.radio.data.LobbyData
import com.github.fribourgsdp.radio.data.LobbyDataKeys
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.database.Database

class PublicLobbiesAdapter(private val context: Context, private val db: Database): RecyclerView.Adapter<PublicLobbiesAdapter.ViewHolder>() {
    private var lobbies: List<LobbyData>? = null
    private var listener: OnPickListener? = null
    private var lastSortedKey = LobbyDataKeys.ID

    init {
        db.getPublicLobbies().addOnSuccessListener {
            lobbies = it
            sortBy(lastSortedKey)

            // update the whole list
            notifyDataSetChanged()
        }.addOnFailureListener {
            lobbies = null
        }

        // init update listener
        db.listenToPublicLobbiesUpdate { value, _ ->
            lobbies = value
            sortBy(lastSortedKey)

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

    override fun getItemId(position: Int): Long {
        return lobbies?.get(position)?.id ?: super.getItemId(position)
    }

    /**
     * Sort the values in the [adapter][PublicLobbiesAdapter] by the given [key].
     */
    fun sortBy(key: LobbyDataKeys) {
        lastSortedKey = key

        lobbies = when (key) {
            LobbyDataKeys.ID -> lobbies?.sortedBy { it.id }
            LobbyDataKeys.NAME -> lobbies?.sortedBy { it.name }
            LobbyDataKeys.HOSTNAME -> lobbies?.sortedBy { it.hostName }
        }

        if (lobbies != null) {
            notifyDataSetChanged()
        }
    }

    /**
     * Set the [listener][l].
     */
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

    /**
     * An interface creating listeners able to handle a pick of lobby.
     */
    fun interface OnPickListener {
        /**
         * Execute an action when a lobby is picked.
         * @param lobbyId the [id][Long] of the picked lobby.
         */
        fun onPick(lobbyId: Long)
    }
}