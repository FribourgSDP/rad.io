package com.github.fribourgsdp.radio.data.view

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import com.github.fribourgsdp.radio.data.Song
import com.google.android.material.snackbar.Snackbar

class SongSwipeHelper (val recyclerView: RecyclerView, private val songList : MutableList<Song>, val rootView: View) : ItemTouchHelper.Callback() {
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

        val deleteConfirmation = Snackbar.make(recyclerView,
            "Do you want to delete this song ?", Snackbar.LENGTH_LONG) //TODO use strings
        deleteConfirmation.addCallback(object : Snackbar.Callback () {
            @SuppressLint("ClickableViewAccessibility")
            override fun onShown(sb: Snackbar?) {
                rootView.setOnTouchListener(DismissTouchListener(deleteConfirmation, rootView))
                recyclerView.setOnTouchListener(DismissTouchListener(deleteConfirmation, recyclerView))
                super.onShown(sb)
            }

            @SuppressLint("ClickableViewAccessibility")
            override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                // will emptily fire if item was removed but does not affect anything
                recyclerView.adapter!!.notifyItemChanged(position)

                rootView.setOnTouchListener(null)
                recyclerView.setOnTouchListener(null)
                super.onDismissed(transientBottomBar, event)
            }
        })
        deleteConfirmation.setAction("Delete", View.OnClickListener {
            songList.removeAt(position)
            recyclerView.adapter!!.notifyItemRemoved(position)
        })
        deleteConfirmation.show()
    }

    /**
     * This listener is used to dismiss the deletion snackBar (cancelling the deletion)
     * if the user touches anything other than the snackBar.
     * it can therefore be attached to onTouchlistener of any UI elements the user
     * is susceptible of pressing if they want to cancel the deletion.
     * The listener can be reversed with _..setOnTouchListener(null)
     */
    class DismissTouchListener(private val snackBar: Snackbar, private val view: View) : View.OnTouchListener {
        override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
            snackBar.dismiss()
            view.performClick()
            return true
        }
    }
}