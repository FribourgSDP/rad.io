package com.github.fribourgsdp.radio.game.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.text.HtmlCompat
import androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY
import androidx.fragment.app.DialogFragment
import com.github.fribourgsdp.radio.R

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
        text.text = HtmlCompat.fromHtml(lyrics, FROM_HTML_MODE_LEGACY)
        closeButton = rootView.findViewById(R.id.close_popup_button)
        closeButton.setOnClickListener{
            //dismiss
            this.dismiss()
        }
        return rootView
    }
}