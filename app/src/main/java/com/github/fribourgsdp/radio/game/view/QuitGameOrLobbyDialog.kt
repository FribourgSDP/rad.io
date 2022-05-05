package com.github.fribourgsdp.radio.game.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.github.fribourgsdp.radio.R


class QuitGameOrLobbyDialog(val ctx: Context): DialogFragment() {
    private val buttons = ArrayList<Button>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_quit_lobby_or_game, container, false)

        // Add the buttons of the view to the list
        buttons.add(rootView.findViewById(R.id.cancelQuitGameOrLobby))
        buttons[0].setOnClickListener {
            setFragmentResult("quitRequest", bundleOf("hasQuit" to false))
            dismiss()
        }
        buttons.add(rootView.findViewById(R.id.validateQuitGameOrLobby))
        buttons[1].setOnClickListener {
            setFragmentResult("quitRequest", bundleOf("hasQuit" to true))
            dismiss()
        }
        return rootView
    }
}