package com.krihl4n.messages

data class RejoinGameRequest(
    val gameId: String = "",
    val playerId: String = "",
)
