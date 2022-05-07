package com.github.fribourgsdp.radio.game.handler

import com.github.fribourgsdp.radio.database.Database
import com.github.fribourgsdp.radio.game.GameView
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener

abstract class GameHandler(private val view: GameView, protected val db: Database) {

    /**
     * Update its [GameView] and/or [Database] with the information in the [snapshot]
     */
    abstract fun handleSnapshot(snapshot: DocumentSnapshot?)

    /**
     * Link the [GameHandler] to the [Database]
     */
    abstract fun linkToDatabase()

    protected fun executeOnUpdate(): EventListener<DocumentSnapshot> {
        return EventListener<DocumentSnapshot> { snapshot, e ->
            if (e != null) {
                view.displayError("An error occurred")
            }

            this.handleSnapshot(snapshot)

        }
    }

}