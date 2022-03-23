package com.github.fribourgsdp.radio

import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks

class LocalDatabase : Database {
    val userMap: MutableMap<String, User> = mutableMapOf()
    val songMap: MutableMap<String,Song> = mutableMapOf()
    val playlistMap: MutableMap<String,Playlist> = mutableMapOf()


    override fun getUser(userId: String): Task<User?> {
        return Tasks.forResult(userMap[userId])
    }

    override fun setUser(userId: String, user: User) {
        userMap[userId] = user
    }

    override fun getSong(songName: String): Task<Song?> {
        return Tasks.forResult(songMap[songName])
    }

    override fun registerSong(song: Song) {
        songMap[song.name] = song
    }

    override fun getPlaylist(playlistName: String): Task<Playlist?> {
        return Tasks.forResult(playlistMap[playlistName])
    }

    override fun registerPlaylist(playlist: Playlist) {
        playlistMap[playlist.name] = playlist
    }
}