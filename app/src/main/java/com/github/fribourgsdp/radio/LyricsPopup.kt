package com.github.fribourgsdp.radio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment

class LyricsPopup(private val lyrics : String) : DialogFragment() {
    private lateinit var closeButton : ImageView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.popup_lyrics, container, false)
        closeButton = rootView.findViewById(R.id.close_popup_button)
        closeButton.setOnClickListener{
            //dismiss
            this.dismiss()
        }
        return rootView
    }
}