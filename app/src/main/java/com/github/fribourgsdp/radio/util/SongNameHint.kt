package com.github.fribourgsdp.radio.util

import java.text.Normalizer

class SongNameHint(val songName : String) {

    private var songNameHint : String
    private val letterIndex : List<Int>
    private var nbLetterAdd = 0

    init {
        songNameHint = replaceWithLine(stripAccents(songName))
        letterIndex = indexOfLetter(songNameHint).shuffled()
    }

    fun addALetter(){
        if(nbLetterAdd < letterIndex.size){
            val index = letterIndex[nbLetterAdd]
            nbLetterAdd++
            songNameHint = songNameHint.substring(0, index) + songName[index] + songNameHint.substring(index + 1)
        }
    }

    fun length():Int{
        return songName.length
    }

    override fun toString(): String {
        return songNameHint
    }


    fun stripAccents(s : String): String {
        var s2 = Normalizer.normalize(s, Normalizer.Form.NFD)
        s2 = s2.replace("[\\p{InCombiningDiacriticalMarks}]".toRegex(), "")
        return s2
    }

    fun replaceWithLine(s : String):String{
        val re = Regex("[A-Za-z0-9]")
        return re.replace(s, "_")
    }

    fun indexOfLetter(s : String) : List<Int>{
        var index: Int = s.indexOf("_")
        val list = ArrayList<Int>()
        while (index >= 0) {
            list.add(index)
            index = s.indexOf("_", index + 1)
        }
        return list
    }
}
