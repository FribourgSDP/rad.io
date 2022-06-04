package com.github.fribourgsdp.radio.util

import com.github.fribourgsdp.radio.database.*
import com.google.firebase.firestore.DocumentSnapshot

fun <T> DocumentSnapshot.getAndCast(field : String) : T{
    return this.get(field)!! as T
}
fun DocumentSnapshot.getPermissions() : HashMap<String, Boolean>{
    return this.getAndCast(PERMISSIONS_KEY)
}
fun DocumentSnapshot.getPlayerFoundMap() : HashMap<String, Boolean>{
    return this.getAndCast(PLAYER_FOUND_MAP_KEY)
}
fun DocumentSnapshot.getPlayerDoneMap() : HashMap<String, Boolean>{
    return this.getAndCast(PLAYER_DONE_MAP_KEY)
}
fun DocumentSnapshot.getPlayers() : HashMap<String, String>{
    return this.getAndCast(PLAYERS_KEY)
}
fun <T> DocumentSnapshot.getScoresOfRound() : HashMap<String, T>{
    return this.getAndCast(SCORES_OF_ROUND_KEY)
}