package com.github.fribourgsdp.radio.database

interface DatabaseHolder {
    fun initializeDatabase() : Database {
        return FirestoreDatabase()
    }
}