package com.github.fribourgsdp.radio

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ScoresAdapter: RecyclerView.Adapter<ScoresAdapter.ViewHolder>() {
    private var scoresList: List<Pair<String, Long>> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.card_score, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (name, score) = scoresList[position]

        holder.itemName.text = name
        holder.itemScore.text = score.toString()
    }

    override fun getItemCount(): Int {
        return scoresList.size
    }

    /**
     * Updates the [list of scores][scores].
     * @param scores A list of [pairs][Pair] of [name][String] and its corresponding [score][Long]
     */
    fun updateScore(scores: List<Pair<String, Long>>) {
        // Sort in decreasing order
        scoresList = scores.sortedBy { (_, score) -> -score}

        // Update the whole list
        this.notifyDataSetChanged()
    }

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        internal val itemName:  TextView = itemView.findViewById(R.id.nameScoreTextView)
        internal val itemScore: TextView = itemView.findViewById(R.id.scoreTextView)
    }
}