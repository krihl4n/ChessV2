package com.krihl4n.messages

data class Move(
    val gameId: String = "",
    val playerId: String = "",
    val from: String = "",
    val to: String = "",
    val pawnPromotion: String? = null
)

