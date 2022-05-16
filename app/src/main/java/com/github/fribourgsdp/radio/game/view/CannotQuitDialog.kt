package com.github.fribourgsdp.radio.game.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.github.fribourgsdp.radio.R
import kotlin.random.Random


class CannotQuitDialog(val ctx: Context): DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_quit_lobby_or_game, container, false)
        val closeButton: Button = rootView.findViewById(R.id.cancelQuitGame)
        closeButton.setOnClickListener {
            dismiss()
        }
        val dontQuitMessage: TextView = rootView.findViewById(R.id.cantQuitGameMessage)
        dontQuitMessage.text = getRandomDontQuitSentence()

        return rootView
    }

    /* Returns one of many random sentences that tell the player he cannot quit.*/
    private fun getRandomDontQuitSentence(): String {
        val cantQuitMessages = resources.getStringArray(R.array.dont_quit_now_messages)
        return cantQuitMessages[Random.nextInt(cantQuitMessages.size - 1)]
    }
}