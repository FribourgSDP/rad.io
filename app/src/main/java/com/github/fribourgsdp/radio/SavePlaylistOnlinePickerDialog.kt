package com.github.fribourgsdp.radio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment

class SavePlaylistOnlinePickerDialog( private val listener: OnPickListener): DialogFragment() {


    private val buttons = ArrayList<Button>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_playlist_save_online_picker, container, false)

        // Add the buttons of the view to the list
        rootView.findViewById<Button>(R.id.saveOnlinePlaylist).setOnClickListener{
            listener.onPick(true)
            dismiss()
        }
       rootView.findViewById<Button>(R.id.saveOfflinePlaylist).setOnClickListener{
            listener.onPick(false)
            dismiss()
        }
        return rootView
    }


    interface OnPickListener{
        fun onPick(online: Boolean)

    }

}