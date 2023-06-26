package com.krihl4n.app.messages

data class RejoinGameRequest(
    val gameId: String = "",
    val playerId: String = "",
)
