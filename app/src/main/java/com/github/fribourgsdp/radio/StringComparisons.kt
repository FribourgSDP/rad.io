package com.github.fribourgsdp.radio

import java.text.Normalizer

class StringComparisons {
    companion object{
        private val chars = "abcdefghijklmnopqrstuvwxyz1234567890"
        fun compareAndGetPoints(actual : String, expected: String, maxPts: Int = 100) : Int {
            val oneErrorPenalty = 0.9
            val twoErrorsPenalty = 0.8
            if (compare(actual, expected, 0)){
                return maxPts
            } else if (compare(actual, expected, 1)){
                return (maxPts * oneErrorPenalty).toInt()
            } else if (compare(actual, expected, 2)){
                return (maxPts * twoErrorsPenalty).toInt()
            } else return 0
        }
        fun compare(actual : String, expected : String, tolerance : Int = 0) : Boolean {
            val s1 = actual.replace(Regex("[!-/]|[:-@]|[\\[-`]|[{-~]| +"), "").lowercase().unaccent()
            val s2 = expected.replace(Regex("[!-/]|[:-@]|[\\[-`]|[{-~]| +"), "").lowercase().unaccent()
            return equal(s1, s2, tolerance)
        }
        private fun CharSequence.unaccent() : String{
            val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
            return "\\p{InCombiningDiacriticalMarks}+".toRegex().replace(temp, "")
        }
        private fun equal(s1 : String, s2 : String, tolerance: Int) : Boolean {
            if (tolerance >= 0){
                if (s1 == s2){
                    return true
                }
            }
            if (tolerance >= 1){
                if (s1.length == s2.length){
                    for (i in s1.indices){
                        for (c in chars){
                            if (equal(s1.substring(0, i) + c + s1.substring(i+1, s1.length), s2, tolerance-1)){
                                return true
                            }
                        }
                    }
                }else if (s1.length < s2.length){
                    for (i in s2.indices){
                        if (equal(s1, s2.substring(0, i) + s2.substring(i+1, s2.length), tolerance-1)){
                            return true
                        }
                    }
                }else if (s1.length > s2.length){
                    for (i in s2.indices){
                        for (c in chars) {
                            if (equal(s1, s2.substring(0, i) + c + s2.substring(i, s2.length), tolerance-1)) {
                                return true
                            }
                        }
                    }
                }
            }
            return false
        }
    }
}