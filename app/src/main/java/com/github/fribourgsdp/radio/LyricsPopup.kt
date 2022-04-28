package com.github.fribourgsdp.radio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class LyricsPopup(private val lyrics : String) : DialogFragment() {
    private lateinit var closeButton : ImageView
    private lateinit var text : TextView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.popup_lyrics, container, false)
        text = rootView.findViewById(R.id.lyricsPopupTextView)
        text.text = lyrics
        closeButton = rootView.findViewById(R.id.close_popup_button)
        closeButton.setOnClickListener{
            //dismiss
            this.dismiss()
        }
        return rootView
    }
}