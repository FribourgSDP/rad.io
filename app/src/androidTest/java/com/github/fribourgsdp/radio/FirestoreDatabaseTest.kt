package com.github.fribourgsdp.radio

import android.content.ContentValues
import android.util.Log
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.ktx.userProfileChangeRequest
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import org.junit.Test
import java.util.concurrent.TimeUnit
import org.junit.Assert.*
import org.junit.Before
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.*

class FirestoreDatabaseTest {
    private val userAuthUID = "tnestUserUser"
    private lateinit var mockFirestoreRef: FirestoreRef
    private lateinit var mockSnapshot: DocumentSnapshot
    private lateinit var mockDocumentReference : DocumentReference
    private lateinit var db : FirestoreDatabase
    @Before
    fun setup(){
        mockFirestoreRef = mock(FirestoreRef::class.java)
        mockSnapshot = mock(DocumentSnapshot::class.java)
        mockDocumentReference = mock(DocumentReference::class.java)
        db = FirestoreDatabase(mockFirestoreRef)

        //this handling is always the same, what changes is what the document snapshot returns
        `when`(mockDocumentReference.get()).thenReturn(Tasks.forResult(mockSnapshot))
        `when`(mockDocumentReference.set(anyMap<String,String>())).thenReturn( Tasks.whenAll(listOf(Tasks.forResult(3))))

        `when`(mockFirestoreRef.getUserRef(userAuthUID)).thenReturn(mockDocumentReference)

    }
    @Test
    fun registeringUserAndFetchingItWorks(){
        val name = "nathanael"
        val userTest = User(name)

        `when`(mockSnapshot.exists()).thenReturn(true)
        `when`(mockSnapshot.get("first")).thenReturn("nathanael")

        db.setUser(userAuthUID,userTest)
        val t1 = db.getUser(userAuthUID)

        assertEquals(name,Tasks.await(t1).name)
    }

    @Test
    fun fetchingUnregisteredUserReturnsNull(){
        val db = FirestoreDatabase()
        val user = Tasks.withTimeout(db.getUser("rubbish"),10,TimeUnit.SECONDS)
        Log.d(ContentValues.TAG, "DocumentSnapshot added with ID put: " + Tasks.await(user))
        assertEquals( null,Tasks.await(user))
    }

}