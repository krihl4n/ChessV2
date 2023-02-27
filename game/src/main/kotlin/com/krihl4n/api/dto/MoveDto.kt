package com.krihl4n.api.dto

data class MoveDto(
    val playerId: String,
    val from: String,
    val to: String,
    val pawnPromotion: String?
)