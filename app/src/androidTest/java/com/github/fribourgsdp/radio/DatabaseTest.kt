package com.github.fribourgsdp.radio

import android.content.ContentValues
import android.util.Log
import com.google.android.gms.tasks.Tasks
import org.junit.Test
import java.util.concurrent.TimeUnit

class DatabaseTest {
    private val userAuthUID = "testUser"
    /*@BeforeAll
    fun beforeAll() {
        val db = Database()
    }*/
    @Test
    fun registeringUsingAndFetchingItWorks(){
        val db = Database()
        //FirebaseAuth.getInstance().currentUser?.uid
        // Create a new user with a first and last name
        var name = "TestingThings"
        val userTest = User(name)
        db.setUser(userAuthUID,userTest)

        var user = Tasks.withTimeout(db.getUser(userAuthUID),10,TimeUnit.SECONDS)
        assert(Tasks.await(user).name == name)
    }
    @Test
    fun fetchingUnregisteredUserReturnsNull(){

        val db = Database()
        var user = Tasks.withTimeout(db.getUser("rubbish"),10,TimeUnit.SECONDS)
        Log.d(ContentValues.TAG, "DocumentSnapshot added with ID put: " + Tasks.await(user))
        assert(Tasks.await(user).name == "null")
    }

}