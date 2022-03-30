package com.github.fribourgsdp.radio

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.EventListener

abstract class GameHandler(private val game: Game, private val view: GameView, protected val db: Database = FirestoreDatabase()) {
    protected abstract fun executeOnUpdate() : EventListener<DocumentSnapshot>

    fun linkToDatabase() {
        db.listenToGameUpdate(game.id, this.executeOnUpdate())
    }

}