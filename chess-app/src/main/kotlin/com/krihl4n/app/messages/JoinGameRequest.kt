package com.krihl4n.app.messages

data class JoinGameRequest(
    val gameId: String = "",
    val colorPreference: String? = null,
    val playerId: String? = null
)
