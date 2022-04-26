package com.github.fribourgsdp.radio

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EndGameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_end_game)

        intent.getSerializableExtra(SCORES_KEY)?.let {
            val scores = it as ArrayList<Pair<String, Long>>
            val manager = LinearLayoutManager(this)

            findViewById<RecyclerView?>(R.id.endScoresRecyclerView).apply {
                layoutManager = manager
                adapter = ScoresAdapter().apply {
                    updateScore(scores)
                }
            }
        }
    }
}