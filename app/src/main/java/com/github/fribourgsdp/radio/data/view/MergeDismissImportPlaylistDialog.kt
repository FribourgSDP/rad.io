package com.github.fribourgsdp.radio.data.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.github.fribourgsdp.radio.R

class MergeDismissImportPlaylistDialog (private val listener: OnPickListener) : DialogFragment()
{

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_playlist_merge_import_dismiss_picker, container, false)

        // Add the buttons of the view to the list
        rootView.findViewById<Button>(R.id.mergePlaylistButton).setOnClickListener{
            listener.onPick(Choice.MERGE)
            dismiss()
        }
        rootView.findViewById<Button>(R.id.importPlaylistButton).setOnClickListener{
            listener.onPick(Choice.IMPORT)
            dismiss()
        }
        rootView.findViewById<Button>(R.id.dismissOnlineButton).setOnClickListener{
            listener.onPick(Choice.DISMISS_ONLINE)
            dismiss()
        }
        return rootView
    }

    interface OnPickListener{
        fun onPick(choice: Choice)
    }

    enum class Choice {
        MERGE, IMPORT, DISMISS_ONLINE
    }
}