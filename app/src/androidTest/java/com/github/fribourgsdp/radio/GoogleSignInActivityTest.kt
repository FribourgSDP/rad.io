package com.github.fribourgsdp.radio

import android.content.*
import androidx.test.espresso.Espresso
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import android.os.*
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.*
import org.hamcrest.Matchers
import androidx.test.espresso.action.ViewActions.click
import java.lang.Exception


/**
 * Main Activity Tests
 *
 */
@RunWith(AndroidJUnit4::class)
class GoogleSignInActivityTest {


    @Test
    fun correctTextOnTextView() {
        val context: Context = ApplicationProvider.getApplicationContext()
        val intent: Intent = Intent(context, GoogleSignInActivity::class.java)
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
        val intent: Intent = Intent(context, GoogleSignInActivity::class.java)
        ActivityScenario.launch<GoogleSignInActivity>(intent).use { scenario ->

            val googleButton = Espresso.onView(ViewMatchers.withId(R.id.googleSignInButton))
            googleButton.perform(click())
        }
    }

    @Test
    fun firebaseAuthWithNewUser() {


        EmailAuthProvider.getCredential("test", "test2")

        val context: Context = ApplicationProvider.getApplicationContext()
        val intent: Intent = Intent(context, GoogleSignInActivity::class.java)
        ActivityScenario.launch<GoogleSignInActivity>(intent).use { scenario ->

            val mockAdditionalUserInfo = MockAdditionalUserInfo(true)
            val mockAuthResult = MockAuthResult(mockAdditionalUserInfo)
            val firebaseOptions = FirebaseOptions.fromResource(context)
            val mockFirebaseApp = MockFirebaseApp(context, "test", firebaseOptions)
            val mockFireBaseAuth = MockFireBaseAuth(false, mockAuthResult, mockFirebaseApp)
            val mockAuthCredential: AuthCredential = MockAuthCredential()

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
        val intent: Intent = Intent(context, GoogleSignInActivity::class.java)
        ActivityScenario.launch<GoogleSignInActivity>(intent).use { scenario ->

            val mockAdditionalUserInfo = MockAdditionalUserInfo(false)
            val mockAuthResult = MockAuthResult(mockAdditionalUserInfo)
            val firebaseOptions = FirebaseOptions.fromResource(context)

            val mockFirebaseApp = MockFirebaseApp(context, "test", firebaseOptions)

            val mockFireBaseAuth = MockFireBaseAuth(false, mockAuthResult, mockFirebaseApp)
            val mockAuthCredential: AuthCredential = MockAuthCredential()

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
        val intent: Intent = Intent(context, GoogleSignInActivity::class.java)
        ActivityScenario.launch<GoogleSignInActivity>(intent).use { scenario ->

            val mockAdditionalUserInfo = MockAdditionalUserInfo(false)
            val mockAuthResult = MockAuthResult(mockAdditionalUserInfo)
            val firebaseOptions = FirebaseOptions.fromResource(context)


            val mockFirebaseApp = MockFirebaseApp(context, "test", firebaseOptions)

            val mockFireBaseAuth = MockFireBaseAuth(true, mockAuthResult, mockFirebaseApp)
            val mockAuthCredential: AuthCredential = MockAuthCredential()


            scenario.onActivity { a ->
                a.firebaseAuthWithCredentitial(
                    mockAuthCredential,
                    mockFireBaseAuth
                )
            }

        }
    }

    @Test
    fun StartActivityWhenAlreadyLogged() {

        Intents.init()
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signInWithEmailAndPassword("test@test.com", "test123!!!")
        Thread.sleep(1000)

        val context: Context = ApplicationProvider.getApplicationContext()

        val intent: Intent = Intent(context, GoogleSignInActivity::class.java)

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

class MockFireBaseAuth(val isFail: Boolean, val mockAuthResult: MockAuthResult, p0: FirebaseApp) :
    FirebaseAuth(p0) {
    override fun signInWithCredential(authCredential: AuthCredential): Task<AuthResult> {
        if (isFail) {

            return Tasks.forException(Exception())

        } else {
            return Tasks.forResult(mockAuthResult)
        }
    }


}


class MockAdditionalUserInfo(val isNew: Boolean) : AdditionalUserInfo {
    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        TODO("Not yet implemented")
    }

    override fun getProviderId(): String? {
        TODO("Not yet implemented")
    }

    override fun getUsername(): String? {
        TODO("Not yet implemented")
    }

    override fun getProfile(): MutableMap<String, Any> {
        TODO("Not yet implemented")
    }

    override fun isNewUser(): Boolean {
        return isNew
    }

}


class MockAuthResult(val userInfo: AdditionalUserInfo) : AuthResult {


    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(p0: Parcel?, p1: Int) {
        TODO("Not yet implemented")
    }

    override fun getAdditionalUserInfo(): AdditionalUserInfo? {
        return userInfo
    }

    override fun getCredential(): AuthCredential? {
        TODO("Not yet implemented")
    }

    override fun getUser(): FirebaseUser? {
        TODO("Not yet implemented")
    }
}

class MockAuthCredential : OAuthCredential() {
    override fun writeToParcel(p0: Parcel?, p1: Int) {
        TODO("Not yet implemented")
    }

    override fun getProvider(): String {
        TODO("Not yet implemented")
    }

    override fun getSignInMethod(): String {
        TODO("Not yet implemented")
    }

    override fun zza(): AuthCredential {
        TODO("Not yet implemented")
    }

    override fun getAccessToken(): String? {
        TODO("Not yet implemented")
    }

    override fun getIdToken(): String? {
        TODO("Not yet implemented")
    }

    override fun getSecret(): String? {
        TODO("Not yet implemented")
    }


}


class MockFirebaseApp(applicationContext: Context?, name: String?, options: FirebaseOptions?) :
    FirebaseApp(applicationContext, name, options) {


}