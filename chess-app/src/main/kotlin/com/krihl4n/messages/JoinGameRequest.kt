package com.krihl4n.messages

data class JoinGameRequest(
    val gameId: String = "",
    val colorPreference: String? = null,
    val playerId: String? = null,
    val rejoin: Boolean = false
)
