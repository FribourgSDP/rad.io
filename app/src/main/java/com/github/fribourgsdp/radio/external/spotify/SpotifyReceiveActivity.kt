package com.github.fribourgsdp.radio.external.spotify

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import com.github.fribourgsdp.radio.R
import com.github.fribourgsdp.radio.config.MyAppCompatActivity
import com.github.fribourgsdp.radio.data.view.UserProfileActivity
import com.spotify.sdk.android.auth.AuthorizationResponse

class SpotifyReceiveActivity : MyAppCompatActivity() {

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

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, UserProfileActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        fun handleSpotifyResponse(intent: Intent?): String {
            if (intent != null){
                val uri: Uri? = intent.data
                if (uri != null) {
                    val response: AuthorizationResponse = AuthorizationResponse.fromUri(uri)

                    return when (response.type) {
                        AuthorizationResponse.Type.TOKEN -> {
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