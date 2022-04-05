package com.github.fribourgsdp.radio

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener

abstract class GameHandler(private val view: GameView, protected val db: Database) {

    abstract fun handleSnapshot(snapshot: DocumentSnapshot?)

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