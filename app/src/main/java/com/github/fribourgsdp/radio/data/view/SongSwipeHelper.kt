package com.github.fribourgsdp.radio.data.view

import android.content.Context
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
        val removedSong = songList[position]
        songList.removeAt(position)
        recyclerView.adapter!!.notifyItemRemoved(position)

        val undoDeletion = Snackbar.make(recyclerView,
            context.resources.getString(R.string.deleteUndoQuestion),
            Snackbar.LENGTH_LONG)

        undoDeletion.setAction(context.resources.getString(R.string.deleteUndoAnswer),
            View.OnClickListener {
                songList.add(position, removedSong)
                recyclerView.adapter!!.notifyItemInserted(position)
        })
        undoDeletion.show()
    }
}