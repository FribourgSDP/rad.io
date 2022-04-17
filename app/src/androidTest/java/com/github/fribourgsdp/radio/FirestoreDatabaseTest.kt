package com.github.fribourgsdp.radio

import android.content.ContentValues
import android.util.Log
import com.google.android.gms.tasks.Tasks
import org.junit.Test
import java.util.concurrent.TimeUnit
import org.junit.Assert.*
class FirestoreDatabaseTest {
    private val userAuthUID = "testUser"
   
    @Test
    fun registeringUsingAndFetchingItWorks(){
        val db = FirestoreDatabase()
        val name = "TestingThings"
        val userTest = User(name)
        db.setUser(userAuthUID,userTest)
        val user = Tasks.withTimeout(db.getUser(userAuthUID),10,TimeUnit.SECONDS)
        assertEquals(name,Tasks.await(user).name)
    }

    @Test
    fun fetchingUnregisteredUserReturnsNull(){
        val db = FirestoreDatabase()
        val user = Tasks.withTimeout(db.getUser("rubbish"),10,TimeUnit.SECONDS)
        Log.d(ContentValues.TAG, "DocumentSnapshot added with ID put: " + Tasks.await(user))
        assertEquals( null,Tasks.await(user))
    }

    @Test
    fun modifyingPermissionsWorks(){
        val db = FirestoreDatabase()
        val user = User("nathan")
        user.id = "215"
        val lobbyId = 123456789L
        val result = Tasks.withTimeout(db.modifyUserMicPermissions(lobbyId, user, true), 10, TimeUnit.SECONDS)
        assertEquals(null, Tasks.await(result))
    }

}