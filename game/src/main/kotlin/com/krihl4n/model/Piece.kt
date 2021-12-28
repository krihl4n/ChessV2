package com.krihl4n.model

internal data class Piece(val color: Color, val type: Type)

enum class Color {
    WHITE, BLACK
}

enum class Type {
    PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
}
