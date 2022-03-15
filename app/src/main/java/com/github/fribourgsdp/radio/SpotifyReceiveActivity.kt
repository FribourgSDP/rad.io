package com.github.fribourgsdp.radio

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.spotify.sdk.android.auth.AuthorizationResponse

class SpotifyReceiveActivity : AppCompatActivity() {

    private lateinit var ACCESS_TOKEN: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spotify_receive)
        handleSpotifyResponse()


        val tokenButton = findViewById<Button>(R.id.getTokenButton)
        tokenButton.setOnClickListener {
            println("Acces token is $ACCESS_TOKEN")
            val intent = Intent(this@SpotifyReceiveActivity, PrintTokenActivity::class.java)
            intent.putExtra("auth_token", ACCESS_TOKEN)
            startActivity(intent)
        }

    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleSpotifyResponse()
    }

    private fun handleSpotifyResponse() {
        val intent: Intent = intent
        if (intent != null){
            val uri: Uri? = intent.data
            if (uri != null) {
                val response: AuthorizationResponse = AuthorizationResponse.fromUri(uri)

                when (response.type) {
                    AuthorizationResponse.Type.TOKEN -> {
                        ACCESS_TOKEN = response.accessToken
                    }

                    AuthorizationResponse.Type.ERROR -> {
                        println("Error occured.")
                    }

                    else -> {
                        println("Something unexpected occured.")
                    }
                }

            }
        }
    }
}