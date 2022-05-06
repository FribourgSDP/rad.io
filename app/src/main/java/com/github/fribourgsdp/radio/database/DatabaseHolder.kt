package com.github.fribourgsdp.radio.database

import com.github.fribourgsdp.radio.FirestoreDatabase

interface DatabaseHolder {
    fun initializeDatabase() : Database {
        return FirestoreDatabase()
    }
}