package com.github.fribourgsdp.radio.data.view

import android.annotation.SuppressLint
import android.content.Context
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.data.Song
import com.google.android.material.snackbar.Snackbar

class SongSwipeHelper (val recyclerView: RecyclerView, private val songList : MutableList<Song>,
                       val rootView: View, val context: Context) : ItemTouchHelper.Callback() {
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, LEFT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.absoluteAdapterPosition
        // resets card position back into its slot
        recyclerView.adapter!!.notifyItemChanged(position)

        val deleteConfirmation = Snackbar.make(recyclerView,
            context.resources.getString(R.string.deleteConfirmationQuestion),
            Snackbar.LENGTH_LONG)
        deleteConfirmation.setAction(context.resources.getString(R.string.deleteConfirmationAnswer),
            View.OnClickListener {
                songList.removeAt(position)
                recyclerView.adapter!!.notifyItemRemoved(position)
        })
        deleteConfirmation.show()
    }
}