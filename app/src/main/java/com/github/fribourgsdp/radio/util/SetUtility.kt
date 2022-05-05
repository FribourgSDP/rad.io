package com.github.fribourgsdp.radio.util

import com.github.fribourgsdp.radio.data.Nameable

object SetUtility {
    /**
     * Add an item to a set, after removing it if it was present
     */
    fun <E> addToSet(set : MutableSet<E>, item : E){
        set.removeIf { p : E -> p == item }
        set.add(item)
    }

    /**
     * Add a collection of items to a set, after removing them if they were present
     */
    fun <E> addAllToSet(set : MutableSet<E>, items : Iterable<E>){
        items.forEach{ i : E -> addToSet(set, i) }
    }

    fun <E : Nameable> getNamedFromSet(set : MutableSet<E>, nameable: E) : E {
        val filteredSet = set.filter { value -> value == nameable }
        if (filteredSet.isEmpty()) {
            throw NoSuchElementException()
        } else {
            return filteredSet[0]
        }
    }
}