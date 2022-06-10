package com.github.fribourgsdp.radio.utils

import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.*
import org.mockito.Mockito

fun makeMockFireBaseAuth(isFail: Boolean, mockAuthResult: AuthResult, firebaseUser : FirebaseUser): FirebaseAuth {
    val firebaseAuth: FirebaseAuth = Mockito.mock(FirebaseAuth::class.java)
    Mockito.`when`(firebaseAuth.signInWithCredential(Mockito.any())).thenReturn(
        if (isFail) {

            Tasks.forException(Exception())

        } else {
            Tasks.forResult(mockAuthResult)
        })
    Mockito.`when`(firebaseAuth.currentUser).thenReturn(firebaseUser)
    return firebaseAuth
}


fun makeMockAdditionalUserInfo(isNew: Boolean): AdditionalUserInfo {
    val additionalUserInfo: AdditionalUserInfo = Mockito.mock(AdditionalUserInfo::class.java)
    Mockito.`when`(additionalUserInfo.isNewUser).thenReturn(isNew)
    return additionalUserInfo
}



fun makeMockAuthResult(userInfo: AdditionalUserInfo, firebaseUser : FirebaseUser): AuthResult {
    val mockAuthResult: AuthResult = Mockito.mock(AuthResult::class.java)
    Mockito.`when`(mockAuthResult.additionalUserInfo).thenReturn(userInfo)
    Mockito.`when`(mockAuthResult.user).thenReturn(firebaseUser)
    return mockAuthResult
}


fun makeMockAuthCredential(): AuthCredential {
    return Mockito.mock(AuthCredential::class.java)
}




fun makeMockFirebaseUser(): FirebaseUser {
    val mockFirebaseUser: FirebaseUser = Mockito.mock(FirebaseUser::class.java)
    Mockito.`when`(mockFirebaseUser.email).thenReturn("test")
    Mockito.`when`(mockFirebaseUser.uid).thenReturn("id")
    return mockFirebaseUser
}