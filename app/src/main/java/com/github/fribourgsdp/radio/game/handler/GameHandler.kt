package com.github.fribourgsdp.radio.game.handler

import android.content.Context
import android.util.Log
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.game.GameView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener

abstract class GameHandler(private val ctx: Context, private val view: GameView, protected val db: Database) {

    /**
     * Update its [GameView] and/or [Database] with the information in the [snapshot]
     */
    abstract fun handleSnapshot(snapshot: DocumentSnapshot?)

    /**
     * Link the [GameHandler] to the [Database]
     */
    abstract fun linkToDatabase()

    /**
     * unlink the [GameHandler] from the [Database]
     */
    abstract fun unlinkFromDatabase()

    protected fun executeOnUpdate(): EventListener<DocumentSnapshot> {
        return EventListener<DocumentSnapshot> { snapshot, e ->
            if (e != null) {
                Log.e("GameHandler Error", "In listener: ${e.message}", e)
                view.displayError(ctx.getString(R.string.game_error))
            }

            this.handleSnapshot(snapshot)

        }
    }

}