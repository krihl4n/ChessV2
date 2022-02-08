package com.krihl4n.model

internal data class Piece(val color: Color, val type: Type)

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

    companion object {
        fun of(colorString: String): Color {
            return if (colorString.lowercase() == "white") WHITE
            else BLACK
        }
    }
}

enum class Type {
    PAWN, KNIGHT, BISHOP, ROOK, QUEEN, KING;

    companion object {
        fun of(typeString: String): Type {
            return when (typeString) {
                "pawn" -> PAWN
                "knight" -> KNIGHT
                "bishop" -> BISHOP
                "king" -> KING
                "queen" -> QUEEN
                "rook" -> ROOK
                else -> throw IllegalArgumentException("cannot create piece type from $typeString")
            }
        }
    }
}
