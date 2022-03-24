package com.github.fribourgsdp.radio



import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.github.fribourgsdp.radio.databinding.ActivityMainBinding
import android.widget.ImageButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fireBaseButton = findViewById<Button>(R.id.FireBaseButton)
        fireBaseButton.setOnClickListener{startActivity(Intent(this,FireBaseTestActivity::class.java))}
        val playButton = findViewById<Button>(R.id.playButton)
        playButton.setOnClickListener {startActivity(Intent(this, GameSettingsActivity::class.java))}
        val settingsButton = findViewById<Button>(R.id.settingsButton)
        settingsButton.setOnClickListener {startActivity(Intent(this, SettingsActivity::class.java))}
        val button : Button = findViewById(R.id.button)
        button.setOnClickListener {startActivity(Intent(this, DisplayLyricsActivity::class.java))}
        val profileButton: ImageButton = findViewById(R.id.profileButton)
        profileButton.setOnClickListener {
            startActivity(Intent(this, UserProfileActivity::class.java))
        }

        /** this user allows quick demo's as it is data that is written to the app
         * specific storage and can be easily read without intents */
        val mockUser = User("Saved User", User.generateColor())
        val mockPlaylist1 = Playlist("test playlist", Genre.COUNTRY)
        val mockPlaylist2 = Playlist("empty playlist", Genre.NONE)
        mockPlaylist1.addSongs(setOf(Song("test Song 1", "test artist1"), Song("test Song 2", "test artist2")))
        mockUser.addPlaylists(setOf(mockPlaylist1, mockPlaylist2))
        mockUser.save(this)
    }
}