package com.github.fribourgsdp.radio.data.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.github.fribourgsdp.radio.R

class KeepOrDismissPlaylistDialog (private val listener: OnPickListener) : DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(
            R.layout.fragment_playlist_keep_dismiss_picker,
            container,
            false
        )

        // Add the buttons of the view to the list
        rootView.findViewById<Button>(R.id.keepPlaylistButton).setOnClickListener {
            listener.onPick(Choice.KEEP)
            dismiss()
        }
        rootView.findViewById<Button>(R.id.dismissPlaylistButton).setOnClickListener {
            listener.onPick(Choice.DISMISS)
            dismiss()
        }

        return rootView
    }

    interface OnPickListener {
        fun onPick(choice: Choice)
    }

    enum class Choice {
        KEEP, DISMISS
    }
}