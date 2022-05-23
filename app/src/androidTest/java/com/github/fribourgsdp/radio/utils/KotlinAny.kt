package com.github.fribourgsdp.radio.utils

import org.mockito.Mockito

class KotlinAny {
    companion object{
        fun <T> any(): T {
            Mockito.any<T>()
            return null as T
        }
    }

}