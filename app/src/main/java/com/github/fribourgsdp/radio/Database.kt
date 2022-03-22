package com.github.fribourgsdp.radio


import android.content.ContentValues
import android.content.ContentValues.TAG
import android.util.Log
import androidx.annotation.NonNull
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.lang.Exception

import java.util.concurrent.CompletableFuture

/**
 *
 *This class represents a database. It communicates with the database(Firestore)
 * and translates the result in classes
 *
 * @constructor Creates a database linked to Firestore
 */
class Database {
    private val db = Firebase.firestore

    /**
     * Gets the [User], wrapped in a [Task], linked to the [userId] given, [userId] should be the authentication token
     * @return [User] wrapped in a task, if the [userId] doesn't exists, it returns a null [User]
     *
     */
     fun getUser(userId : String): Task<User> {
         return Tasks.forResult(DocumentSnapshot.())
             /*.continueWith { l ->
             val result = l.result
             if(result.exists()){
                 User(result["first"].toString())
             }else{
                 null
             }*/
        /*return db.collection("user").document(userId).get()
        return  db.collection("user").document(userId).get().continueWith { l ->
            val result = l.result
            if(result.exists()){
                User(result["first"].toString())
            }else{
                null
            }*/
        }
    }

    /**
     * Sets the [User] information in the database and link it the to [userId] given, [userId] should be the authentication token
     */
    fun setUser(userId : String, user : User){
        val userHash = hashMapOf(
            "first" to user.name
        )
        db.collection("user").document(userId).set(userHash)
    }


    fun getSong(songName : String): Task<Song>{
        return  db.collection("songs").document(songName).get().continueWith { l ->
            val result = l.result
            if(result.exists()){
                val songName = result["songName"].toString()
                val artistName = result["artistName"].toString()
                Song(songName,artistName,"")
            }else{
                null
            }
        }
    }

    fun registerSong(song : Song){
        val songHash = hashMapOf(
            "songName" to song.name,
            "artistName" to song.artist
            //todo: add lyrics when it won't be a future anymore
        )
        db.collection("songs").document(song.name).set(songHash)

    }

    fun getPlaylist(playlistName : String): Task<Playlist>{

        return  db.collection("playlists").document(playlistName).get().continueWith { l ->
            val result = l.result
            if(result.exists()){

                val playlistTitle = result["playlistName"].toString()
                val genre =result["genre"].toString()
                val songs = result["songs"].toString()

                //parse the list result to create a set
                val songSet : MutableSet<Song> = mutableSetOf()
                for(song in songs.substring(1,songs.length-1).split(",")){
                    songSet.add((Song(song,"","")))
                }

                Playlist(playlistTitle, songSet, Genre.valueOf(genre))

            }else{
                null
            }
        }
    }

    fun registerPlaylist( playlist : Playlist){
        //We will only store the songName, because you don't want to fetch all the lyrics of all songs when you retrieve the playlist
        val titleList : MutableList<String> = mutableListOf()
        for( song in playlist.getSongs()){
            titleList.add(song.name)
        }
        val playlistHash = hashMapOf(
            "playlistName" to playlist.name,
            "genre" to playlist.genre,
            "songs" to titleList
        )
        db.collection("playlists").document(playlist.name).set(playlistHash)
    }

}