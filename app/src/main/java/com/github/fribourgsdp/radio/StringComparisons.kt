package com.github.fribourgsdp.radio

import java.text.Normalizer

const val MAX_ALLOWED_ERRORS = 2

class StringComparisons {
    companion object{
        private const val chars = "abcdefghijklmnopqrstuvwxyz1234567890"

        fun compareAndGetPoints(actual : String, expected : String, maxPts : Int = 100) : Int {
            val oneErrorPenalty = 0.9
            val twoErrorsPenalty = 0.8
            return when(compare(actual, expected)){
                0 -> maxPts
                1 -> (oneErrorPenalty*maxPts).toInt()
                2 -> (twoErrorsPenalty*maxPts).toInt()
                else -> 0
            }
        }

        fun compare(actual : String, expected : String) : Int {
            val s1 = actual.replace(Regex("[!-/]|[:-@]|[\\[-`]|[{-~]| +"), "").lowercase().unaccent()
            val s2 = expected.replace(Regex("[!-/]|[:-@]|[\\[-`]|[{-~]| +"), "").lowercase().unaccent()
            for (tolerance in 0..MAX_ALLOWED_ERRORS) {
                if( equal(s1, s2, tolerance)){
                    return tolerance
                }
            }
            return -1
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