package com.github.fribourgsdp.radio

import com.google.firebase.firestore.DocumentSnapshot

fun <T> DocumentSnapshot.getAndCast(field : String) : T{
    return this.get(field)!! as T
}