package com.github.fribourgsdp.radio

interface DatabaseHolder {
    fun initializeDatabase() : Database{
        return FirestoreDatabase()
    }
}