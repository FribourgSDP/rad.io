package com.github.fribourgsdp.radio.unit

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.intent.Intents
import com.github.fribourgsdp.radio.activities.*
import com.github.fribourgsdp.radio.mockimplementations.GoogleSignInTestMockUserProfileActivity
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.EmailAuthProvider
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GoogleSignInTest {

    @Before
    fun initIntent() {
        Intents.init()
    }

    @After
    fun releaseIntent() {
        Intents.release()
    }




    @Test
    fun firebaseAuthWithNewUser() {

        EmailAuthProvider.getCredential("test", "test2")

        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, GoogleSignInTestMockUserProfileActivity::class.java)
        ActivityScenario.launch<GoogleSignInTestMockUserProfileActivity>(intent).use { scenario ->

            val mockAdditionalUserInfo = makeMockAdditionalUserInfo(true)
            val mockUser = makeMockFirebaseUser()
            val mockAuthResult = makeMockAuthResult(mockAdditionalUserInfo,mockUser)
            val mockFireBaseAuth = makeMockFireBaseAuth(false, mockAuthResult, mockUser)
            val mockAuthCredential: AuthCredential = makeMockAuthCredential()

            scenario.onActivity { a ->
                a.firebaseAuthWithCredentitial(
                    mockAuthCredential,
                    mockFireBaseAuth
                )

            }

            scenario.onActivity { a ->
                assertEquals(true, a.newLogin)
            }



        }
    }


    @Test
    fun firebaseAuthWithUser() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, GoogleSignInTestMockUserProfileActivity::class.java)
        ActivityScenario.launch<GoogleSignInTestMockUserProfileActivity>(intent).use { scenario ->

            val mockAdditionalUserInfo = makeMockAdditionalUserInfo(false)
            val mockUser = makeMockFirebaseUser()
            val mockAuthResult = makeMockAuthResult(mockAdditionalUserInfo,mockUser)
            val mockFireBaseAuth = makeMockFireBaseAuth(false, mockAuthResult, mockUser)
            val mockAuthCredential: AuthCredential = makeMockAuthCredential()

            scenario.onActivity { a ->
                a.firebaseAuthWithCredentitial(
                    mockAuthCredential,
                    mockFireBaseAuth
                )
            }

            scenario.onActivity { a ->
                assertEquals(true, a.normalLogin)
            }
        }
    }

    @Test
    fun firebaseAuthFail() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, GoogleSignInTestMockUserProfileActivity::class.java)
        ActivityScenario.launch<GoogleSignInTestMockUserProfileActivity>(intent).use { scenario ->

            val mockAdditionalUserInfo = makeMockAdditionalUserInfo(false)
            val mockUser = makeMockFirebaseUser()
            val mockAuthResult = makeMockAuthResult(mockAdditionalUserInfo,mockUser)
            val mockFireBaseAuth = makeMockFireBaseAuth(true, mockAuthResult,mockUser)
            val mockAuthCredential: AuthCredential = makeMockAuthCredential()

            scenario.onActivity { a ->
                a.firebaseAuthWithCredentitial(
                    mockAuthCredential,
                    mockFireBaseAuth
                )
            }

            scenario.onActivity { a ->
                assertEquals(true, a.failLogin)
            }

        }
    }

    @Test
    fun startActivityWhenAlreadyLogged() {


        val context: Context = ApplicationProvider.getApplicationContext()

        val intent = Intent(context, GoogleSignInTestMockUserProfileActivity::class.java)

        ActivityScenario.launch<GoogleSignInTestMockUserProfileActivity>(intent).use { scenario ->

            scenario.onActivity { a ->
                a.signInOrOut()
            }

            scenario.onActivity { a ->
                assertEquals(true, a.alreadyLogin)
            }
        }
    }


}