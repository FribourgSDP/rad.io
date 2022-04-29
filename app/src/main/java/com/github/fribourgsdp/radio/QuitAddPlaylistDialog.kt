package com.github.fribourgsdp.radio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment

class QuitAddPlaylistDialog(val ctx: Context): DialogFragment() {
    private val buttons = ArrayList<Button>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_quit_add_playlist, container, false)

        // Add the buttons of the view to the list
        buttons.add(rootView.findViewById(R.id.cancelQuitAddPlaylist))
        buttons[0].setOnClickListener{
            dismiss()
        }
        buttons.add(rootView.findViewById(R.id.validateQuitAddPlaylist))
        buttons[1].setOnClickListener{
            val intent = Intent(ctx, UserProfileActivity::class.java)
            startActivity(intent)
            dismiss()
        }
        return rootView
    }
}