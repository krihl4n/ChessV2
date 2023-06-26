package com.krihl4n.app.messages

data class UndoMoveRequest(
    val gameId: String = "",
    val playerId: String = "",
)
