package com.github.fribourgsdp.radio

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.*
import org.hamcrest.Matchers
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import java.util.concurrent.TimeUnit


/**
 * Main Activity Tests
 *
 */
@RunWith(AndroidJUnit4::class)
class GoogleSignInActivityTest {


    @Test
    fun correctTextOnTextView() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, GoogleSignInActivity::class.java)
        ActivityScenario.launch<GoogleSignInActivity>(intent).use { scenario ->

            val txtView = onView(ViewMatchers.withId(R.id.captionTv))
            txtView.check(
                ViewAssertions.matches(
                    ViewMatchers.withText("Welcome to Google SignIn")
                )
            )
        }
    }

    @Test
    fun playGoogleButton() {

        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, GoogleSignInActivity::class.java)
        ActivityScenario.launch<GoogleSignInActivity>(intent).use { scenario ->

            val googleButton = onView(ViewMatchers.withId(R.id.googleSignInButton))
            googleButton.perform(click())
        }
    }

    @Test
    fun firebaseAuthWithNewUser() {


        EmailAuthProvider.getCredential("test", "test2")

        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, GoogleSignInActivity::class.java)
        ActivityScenario.launch<GoogleSignInActivity>(intent).use { scenario ->

            val mockAdditionalUserInfo = makeMockAdditionalUserInfo(true)
            val mockUser = makeMockFirebaseUser()
            val mockAuthResult = makeMockAuthResult(mockAdditionalUserInfo,mockUser)
            val mockFireBaseAuth = makeMockFireBaseAuth(false, mockAuthResult)
            val mockAuthCredential: AuthCredential = makeMockAuthCredential()

            Intents.init()
            scenario.onActivity { a ->
                a.firebaseAuthWithCredentitial(
                    mockAuthCredential,
                    mockFireBaseAuth
                )
            }

            Intents.intended(
                Matchers.allOf(
                    IntentMatchers.hasComponent(UserProfileActivity::class.java.name),
                    IntentMatchers.toPackage("com.github.fribourgsdp.radio")
                )
            )

            Intents.release()
        }
    }


    @Test
    fun firebaseAuthWithUser() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, GoogleSignInActivity::class.java)
        ActivityScenario.launch<GoogleSignInActivity>(intent).use { scenario ->

            val mockAdditionalUserInfo = makeMockAdditionalUserInfo(false)
            val mockUser = makeMockFirebaseUser()
            val mockAuthResult = makeMockAuthResult(mockAdditionalUserInfo,mockUser)
            val mockFireBaseAuth = makeMockFireBaseAuth(false, mockAuthResult)
            val mockAuthCredential: AuthCredential = makeMockAuthCredential()

            Intents.init()

            scenario.onActivity { a ->
                a.firebaseAuthWithCredentitial(
                    mockAuthCredential,
                    mockFireBaseAuth
                )
            }

            Intents.intended(
                Matchers.allOf(
                    IntentMatchers.hasComponent(UserProfileActivity::class.java.name),
                    IntentMatchers.toPackage("com.github.fribourgsdp.radio")
                )
            )
            Intents.release()
        }
    }

    @Test
    fun firebaseAuthFail() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val intent = Intent(context, GoogleSignInActivity::class.java)
        ActivityScenario.launch<GoogleSignInActivity>(intent).use { scenario ->

            val mockAdditionalUserInfo = makeMockAdditionalUserInfo(false)
            val mockUser = makeMockFirebaseUser()
            val mockAuthResult = makeMockAuthResult(mockAdditionalUserInfo,mockUser)
            val mockFireBaseAuth = makeMockFireBaseAuth(true, mockAuthResult)
            val mockAuthCredential: AuthCredential = makeMockAuthCredential()

            scenario.onActivity { a ->
                a.firebaseAuthWithCredentitial(
                    mockAuthCredential,
                    mockFireBaseAuth
                )
            }

        }
    }

    @Test
    fun startActivityWhenAlreadyLogged() {

        Intents.init()
        val firebaseAuth = FirebaseAuth.getInstance()
        val task = Tasks.withTimeout(firebaseAuth.signInWithEmailAndPassword("test@test.com", "test123!!!"),10, TimeUnit.SECONDS)
        Tasks.await(task)

        val context: Context = ApplicationProvider.getApplicationContext()

        val intent = Intent(context, GoogleSignInActivity::class.java)

        ActivityScenario.launch<GoogleSignInActivity>(intent).use { scenario ->

            Intents.intended(
                Matchers.allOf(
                    IntentMatchers.hasComponent(UserProfileActivity::class.java.name),
                    IntentMatchers.toPackage("com.github.fribourgsdp.radio")
                )
            )
        }
        Intents.release()
        firebaseAuth.signOut()
    }


}



fun makeMockFireBaseAuth(isFail: Boolean, mockAuthResult: AuthResult): FirebaseAuth {
    val firebaseAuth: FirebaseAuth = mock(FirebaseAuth::class.java)
    `when`(firebaseAuth.signInWithCredential(any())).thenReturn(
        if (isFail) {

            Tasks.forException(Exception())

        } else {
        Tasks.forResult(mockAuthResult)
    })
    return firebaseAuth
}


fun makeMockAdditionalUserInfo(isNew: Boolean): AdditionalUserInfo {
    val additionalUserInfo: AdditionalUserInfo = mock(AdditionalUserInfo::class.java)
    `when`(additionalUserInfo.isNewUser).thenReturn(isNew)
    return additionalUserInfo
}



fun makeMockAuthResult(userInfo: AdditionalUserInfo,firebaseUser : FirebaseUser): AuthResult {
    val mockAuthResult: AuthResult = mock(AuthResult::class.java)
    `when`(mockAuthResult.additionalUserInfo).thenReturn(userInfo)
    `when`(mockAuthResult.user).thenReturn(firebaseUser)
    return mockAuthResult
}


fun makeMockAuthCredential(): AuthCredential {
    return mock(AuthCredential::class.java)
}




fun makeMockFirebaseUser(): FirebaseUser {
    val mockFirebaseUser: FirebaseUser = mock(FirebaseUser::class.java)
    `when`(mockFirebaseUser.email).thenReturn("test")
    `when`(mockFirebaseUser.uid).thenReturn("id")
    return mockFirebaseUser
}