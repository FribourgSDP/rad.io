package com.github.fribourgsdp.radio.mockimplementations

import com.github.fribourgsdp.radio.LyricsGetter
import com.github.fribourgsdp.radio.SongFragment

class MockSongFragment : SongFragment() {
    override fun getLyricsGetter() : LyricsGetter{
        return MockLyricsGetter
    }
}