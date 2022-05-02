package com.github.fribourgsdp.radio

data class LobbyData(val id: Long, val name: String, val hostName: String)

enum class LobbyDataKeys(stringId: Int) {
    ID(R.string.id),
    NAME(R.string.name),
    HOSTNAME(R.string.hostname)
}