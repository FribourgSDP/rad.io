package com.github.fribourgsdp.radio

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.spotify.sdk.android.auth.AuthorizationResponse

class SpotifyReceiveActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spotify_receive)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if (intent != null){
            val uri: Uri? = intent.data
            if (uri != null) {
                val response: AuthorizationResponse = AuthorizationResponse.fromUri(uri)

                when (response.type) {
                    AuthorizationResponse.Type.TOKEN -> {
                        Toast.makeText(applicationContext, response.accessToken, Toast.LENGTH_LONG).show()
                    }

                    AuthorizationResponse.Type.ERROR -> {
                        Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG)
                    }

                    else -> {
                        Toast.makeText(applicationContext, "Unexcpected behaviour", Toast.LENGTH_LONG)
                    }
                }

            }
        }
    }
}