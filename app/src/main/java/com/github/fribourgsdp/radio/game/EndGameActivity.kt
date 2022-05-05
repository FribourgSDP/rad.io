package com.github.fribourgsdp.radio.game

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.fribourgsdp.radio.MainActivity
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.game.view.ScoresAdapter
import com.github.fribourgsdp.radio.config.MyAppCompatActivity

class EndGameActivity : MyAppCompatActivity() {
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

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}