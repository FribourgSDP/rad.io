package com.github.fribourgsdp.radio.util

import com.google.firebase.firestore.DocumentSnapshot

fun <T> DocumentSnapshot.getAndCast(field : String) : T{
    return this.get(field)!! as T
}
fun DocumentSnapshot.getPermissions() : HashMap<String, Boolean>{
    return this.getAndCast("permissions")
}
fun DocumentSnapshot.getPlayerFoundMap() : HashMap<String, Boolean>{
    return this.getAndCast("player_found_map")
}
fun DocumentSnapshot.getPlayerDoneMap() : HashMap<String, Boolean>{
    return this.getAndCast("player_done_map")
}
fun DocumentSnapshot.getPlayers() : HashMap<String, String>{
    return this.getAndCast("players")
}
fun <T> DocumentSnapshot.getScoresOfRound() : HashMap<String, T>{
    return this.getAndCast("scores_of_round")
}