package com.github.fribourgsdp.radio

import android.content.Context

data class LobbyData(val id: Long, val name: String, val hostName: String)

enum class LobbyDataKeys(private val stringId: Int) {
    ID(R.string.id),
    NAME(R.string.name),
    HOSTNAME(R.string.hostname);

    override fun toString(): String {
        return ctx?.getString(stringId) ?: super.toString()
    }

    companion object {
        private var ctx: Context? = null

        fun setContext(context: Context) {
            ctx = context
        }

    }
}