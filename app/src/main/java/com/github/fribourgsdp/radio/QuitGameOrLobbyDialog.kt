package com.github.fribourgsdp.radio

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment


class QuitGameOrLobbyDialog(val ctx: Context, private val isHost: Boolean, private val isFromGameActivity: Boolean, private val user: User, private val db: Database, private val gameID: Long): DialogFragment() {
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
            dismiss()
        }
        buttons.add(rootView.findViewById(R.id.validateQuitGameOrLobby))
        buttons[1].setOnClickListener {
            if (isHost && isFromGameActivity) {
                quitGameForHost()
            }
            else if (isHost && !isFromGameActivity) {
                quitLobbyForHost()
            }
            else {
                quitGameOrLobbyForNonHost()
            }
            dismiss()
        }
        return rootView
    }

    private fun quitGameForHost() {
        db.disableGame(gameID)
        getActivity(ctx)?.finish()
    }

    private fun quitLobbyForHost() {
        db.disableLobby(gameID)
        val intent = Intent(ctx, MainActivity::class.java)
        startActivity(intent)
    }

    private fun quitGameOrLobbyForNonHost() {
        db.removeUserFromLobby(gameID, user)
        if (isFromGameActivity) {
            db.removePlayerFromGame(gameID, user)
        }
        getActivity(ctx)?.finish()
    }

    private fun getActivity(context: Context?): Activity? {
        if (context == null) {
            return null
        } else if (context is ContextWrapper) {
            return if (context is Activity) {
                context as Activity?
            } else {
                getActivity((context as ContextWrapper).baseContext)
            }
        }
        return null
    }

}