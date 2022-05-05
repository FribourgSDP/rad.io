package com.github.fribourgsdp.radio

import android.content.Intent
import android.os.Bundle
import com.github.fribourgsdp.radio.data.Genre
import com.github.fribourgsdp.radio.data.Playlist
import com.github.fribourgsdp.radio.data.Song
import com.github.fribourgsdp.radio.data.User
import com.google.android.gms.tasks.Tasks

class FireBaseTestActivity : MyAppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fire_base_test)


        val db = FirestoreDatabase()
        // val userAuthUID = FirebaseAuth.getInstance().currentUser?.uid // this will be used later
        //when working with an authenticated user

        val userAuthUID = "m0sd9l"
        // Create a new user with a first and last name
        val userNa = User("nathanDuchesne")
        userNa.id = userAuthUID



        val song1 = Song("Song1","victor")
        val t1 = db.generateSongId().addOnSuccessListener { l ->
            song1.id = l.toString()
        }
        val song2 = Song("Song2","Nathan")
        val t2 = db.generateSongId().addOnSuccessListener { l ->
            song2.id = l.toString()
        }
        var playlist = Playlist("TestPlaylist", Genre.FRENCH)
        playlist.addSong(song1)
        playlist.addSong(song2)
        val t3 =  db.generatePlaylistId().addOnSuccessListener { l ->
            playlist.id = l.toString()
        }

        userNa.addPlaylist(playlist)
        Tasks.whenAllComplete(listOf(t1,t2,t3)).addOnSuccessListener { l->
            db.setUser(userAuthUID,userNa)
            db.registerPlaylist(playlist)
        }




        /*

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
        try {
        db.registerSong(song).addOnFailureListener { e ->
            Log.w(ContentValues.TAG, "Error adding document", e)

        }

            db.getSong(song.id).addOnSuccessListener { l ->
                if (l != null) {
                    val text = song.name + " by " + song.artist
                    findViewById<TextView>(R.id.songNameChange).text = text
                }
            }.addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)
            }
        }catch(e : Exception){
                Log.w(ContentValues.TAG, "Error adding document", e)
        }

        val playlist = Playlist("testPlaylist",Genre.COUNTRY)
        val song2 = Song("song2","Victor","")
        val t1 = db.generateSongId().addOnSuccessListener { l ->
            song.id = l.toString()
        }
        val t2 = db.generateSongId().addOnSuccessListener { l ->
            song2.id = l.toString()
        }
        playlist.addSong(song)
        playlist.addSong(song2)
       val t3 =  db.generatePlaylistId().addOnSuccessListener { l ->
            playlist.id = l.toString()
        }

        Tasks.whenAllComplete(listOf(t1,t2,t3)).addOnSuccessListener { l->
            db.registerPlaylist(playlist)
        }.addOnSuccessListener {
            db.getPlaylist(playlist.id).addOnSuccessListener { l ->
                if (l != null) {
                    var text = ""
                    for (songi in l.getSongs()) {
                        text += ";" + songi.name + " by " + songi.artist
                    }
                    findViewById<TextView>(R.id.playlistNameChange).text = text

                } else {
                    findViewById<TextView>(R.id.playlistNameChange).text = "foooote"
                }
            }.addOnFailureListener { e ->
                Log.w(ContentValues.TAG, "Error adding document", e)

            }
        }
            /*.registerPlaylist(playlist).addOnFailureListener { e ->
            Log.w(ContentValues.TAG, "Error adding document", e)

        }*/

        /*db.getPlaylist(playlist.id).addOnSuccessListener { l ->
            if(l != null){
                var text = ""
                for (songi in l.getSongs()) {
                    text += ";" + songi.name + " by " + songi.artist
                }
                findViewById<TextView>(R.id.playlistNameChange).text = text

            }else{
                findViewById<TextView>(R.id.playlistNameChange).text = "foooote"
            }
        }.addOnFailureListener { e ->
            Log.w(ContentValues.TAG, "Error adding document", e)

        }*/

         */
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}




