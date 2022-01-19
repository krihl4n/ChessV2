package com.krihl4n.api

data class PiecePositionUpdateDto(val primaryMove: MoveDto, val secondaryMove: MoveDto? = null)

data class MoveDto(val from: String, val to: String)