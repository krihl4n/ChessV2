package com.krihl4n.messages

data class UndoMoveRequest(
    val gameId: String = "",
    val playerId: String = "",
)
