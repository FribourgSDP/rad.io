package com.github.fribourgsdp.radio

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.DialogFragment
import com.github.fribourgsdp.radio.game.GameView


class SongPickerDialog(private val choices: List<String>, private val listener: GameView.OnPickListener): DialogFragment() {
    private val buttons = ArrayList<Button>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView: View = inflater.inflate(R.layout.fragment_song_picker, container, false)

        // Add the buttons of the view to the list
        buttons.add(rootView.findViewById(R.id.songPick0))
        buttons.add(rootView.findViewById(R.id.songPick1))
        buttons.add(rootView.findViewById(R.id.songPick2))

        val localChoices: MutableList<String?> = choices.toMutableList()

        // Make localChoices and buttons the same size
        for (i in 0 until buttons.size - choices.size) {
            localChoices.add(null)
        }

        assert(localChoices.size == buttons.size)

        // Update buttons with the songs
        for ((button, choice) in buttons.zip(localChoices)) {
            if (choice != null) {
                button.text = choice
                button.setOnClickListener {
                    // On click => give the value to the OnPickListener
                    listener.onPick(button.text.toString())

                    // Close the dialog
                    dismiss()
                }
                button.visibility = View.VISIBLE
            } else {
                // Not as many songs as button => hide them
                button.visibility = View.GONE
            }
        }

        return rootView
    }
}