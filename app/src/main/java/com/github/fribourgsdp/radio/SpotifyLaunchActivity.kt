package com.github.fribourgsdp.radio

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.spotify.sdk.android.auth.AccountsQueryParameters.CLIENT_ID
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse

class SpotifyLaunchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spotify_launch)

        val playButton = findViewById<Button>(R.id.launchSpotifyButton)
        playButton.setOnClickListener {
            authenticateUser()
        }
    }

    private fun authenticateUser() {
        val request =
            AuthorizationRequest.Builder(MY_CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI)
                .setScopes(arrayOf(SCOPES))
                .setShowDialog(true)
                .build()
        println(MY_CLIENT_ID)
        AuthorizationClient.openLoginInBrowser(this, request)
    }
}