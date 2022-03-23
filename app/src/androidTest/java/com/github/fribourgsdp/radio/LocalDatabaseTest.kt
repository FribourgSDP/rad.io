package com.github.fribourgsdp.radio

import com.google.android.gms.tasks.Tasks
import org.junit.Test
import java.util.concurrent.TimeUnit

class LocalDatabaseTest
{
    private val userAuthUID = "testUser"

    @Test
    fun registeringUsingAndFetchingItWorks(){
        val db = LocalDatabase()
        val name = "TestingThings"
        val userTest = User(name)
        db.setUser(userAuthUID,userTest)
        val user = Tasks.withTimeout(db.getUser(userAuthUID),10, TimeUnit.SECONDS)
        assert(Tasks.await(user).name == name)
    }
}