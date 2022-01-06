package com.krihl4n

data class PiecePosition(val field: String = "", val piece: Piece = Piece("", ""))

data class Piece(val color: String = "", val type: String = "")