package com.krihl4n.model

data class Piece(val color: Color, val type: Type)

enum class Color {
    WHITE {
        override fun opposite(): Color {
            return BLACK
        }
    },
    BLACK {
        override fun opposite(): Color {
            return WHITE
        }
    };

    abstract fun opposite(): Color
}

enum class Type {
    PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING
}
