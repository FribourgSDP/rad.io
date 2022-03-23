package com.github.fribourgsdp.radio

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import java.lang.IllegalArgumentException

class LocalDatabase : Database {
    val userMap: MutableMap<String, User> = mutableMapOf()
    val songMap: MutableMap<String,Song> = mutableMapOf()
    val playlistMap: MutableMap<String,Playlist> = mutableMapOf()


    override fun getUser(userId: String): Task<User> {
        return Tasks.forResult(userMap[userId])
    }

    override fun setUser(userId: String, user: User): Task<Void> {
        userMap[userId] = User(user.name)
        return Tasks.forResult(null)
    }

    override fun getSong(songName: String): Task<Song> {
        return Tasks.forResult(songMap[songName])
    }

    override fun registerSong(song: Song): Task<Void> {
        songMap[song.name] = song
        return Tasks.forResult(null)
    }

    override fun getPlaylist(playlistName: String): Task<Playlist> {
        return Tasks.forResult(playlistMap[playlistName])
    }

    override fun registerPlaylist(playlist: Playlist): Task<Void> {
        val titleList : MutableList<Song> = mutableListOf()
        for( song in playlist.getSongs()){
            titleList.add(Song(song.name,song.artist,""))
        }
        playlistMap[playlist.name] = Playlist(playlist.name,titleList.toSet(),playlist.genre)

        return Tasks.forResult(null)
    }
}