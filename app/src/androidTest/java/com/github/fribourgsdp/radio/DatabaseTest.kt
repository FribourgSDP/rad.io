package com.github.fribourgsdp.radio

import android.content.ContentValues
import android.util.Log
import com.google.android.gms.tasks.Tasks
import org.junit.Test
import java.util.concurrent.TimeUnit

class DatabaseTest {
    private val userAuthUID = "testUser"
   
    @Test
    fun registeringUsingAndFetchingItWorks(){
        val db = Database()
        val name = "TestingThings"
        val userTest = User(name)
        db.setUser(userAuthUID,userTest)
        val user = Tasks.withTimeout(db.getUser(userAuthUID),10,TimeUnit.SECONDS)
        assert(Tasks.await(user).name == name)
    }

    @Test
    fun fetchingUnregisteredUserReturnsNull(){
        val db = Database()
        val user = Tasks.withTimeout(db.getUser("rubbish"),10,TimeUnit.SECONDS)
        Log.d(ContentValues.TAG, "DocumentSnapshot added with ID put: " + Tasks.await(user))
        assert(Tasks.await(user) == null)
    }

}