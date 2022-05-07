package com.github.fribourgsdp.radio.mockimplementations

import com.github.fribourgsdp.radio.external.musixmatch.LyricsGetter
import com.github.fribourgsdp.radio.data.view.SongFragment

class MockSongFragment : SongFragment() {
    override fun getLyricsGetter() : LyricsGetter {
        return MockLyricsGetter
    }
}