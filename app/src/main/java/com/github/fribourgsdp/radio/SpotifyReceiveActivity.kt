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
        ACCESS_TOKEN = handleSpotifyResponse(intent)

        val importSpotifyPlaylistsButton = findViewById<Button>(R.id.importSpotifyPlaylistsButton)
        importSpotifyPlaylistsButton.setOnClickListener {
            val intent = Intent(this@SpotifyReceiveActivity, ImportSpotifyPlaylistsActivity::class.java)
            intent.putExtra("auth_token", ACCESS_TOKEN)
            startActivity(intent)
        }

    }

    companion object {
        fun handleSpotifyResponse(intent: Intent?): String {
            if (intent != null){
                val uri: Uri? = intent.data
                if (uri != null) {
                    val response: AuthorizationResponse = AuthorizationResponse.fromUri(uri)

                    return when (response.type) {
                        AuthorizationResponse.Type.TOKEN -> {
                            println(uri)
                            response.accessToken
                        }

                        AuthorizationResponse.Type.ERROR -> {
                            ("Error occured.")
                        }

                        else -> {
                            ("Something unexpected occured.")
                        }
                    }
                }
            }
            return "Error"
        }
    }
}