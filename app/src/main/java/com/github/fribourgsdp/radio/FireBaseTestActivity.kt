package com.github.fribourgsdp.radio

import android.content.ContentValues
import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FireBaseTestActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fire_base_test)


        val db = FirestoreDatabase()
        // val userAuthUID = FirebaseAuth.getInstance().currentUser?.uid // this will be used later
        //when working with an authenticated user

        val userAuthUID = "m0sd9l"
        // Create a new user with a first and last name
        val userNa = User("nathanDuchesne")
        db.setUser(userAuthUID,userNa)

        db.getUser("m0sd9l").addOnSuccessListener { l ->
            if (l == null){
                findViewById<TextView>(R.id.userNameChange).text = "There has been an error"
            }else {
                findViewById<TextView>(R.id.userNameChange).text = l.name
            }
        }.addOnFailureListener { e ->
            findViewById<TextView>(R.id.userNameChange).text = "IL y A UNE FOTE"

            Log.w(ContentValues.TAG, "Error adding document", e)
        }


        val song = Song("this is the title", "Nathan","")
        db.registerSong(song)

        db.getSong(song.name).addOnSuccessListener { l ->
            if( l == null){

            }else{
                val text = song.name + " by " +  song.artist
                findViewById<TextView>(R.id.songNameChange).text = text
            }
        }


        val playlist = Playlist("testPlaylist",Genre.COUNTRY)
        val song2 = Song("song2","Victor","")

        playlist.addSong(song)
        playlist.addSong(song2)
        db.registerPlaylist(playlist)

        db.getPlaylist("testPlaylist").addOnSuccessListener { l ->
            if(l == null){

            }else {
                var text = ""
                for (songi in l.getSongs()) {
                    text += ";" + songi.name
                }
                findViewById<TextView>(R.id.playlistNameChange).text = text

            }
        }
    }
}




