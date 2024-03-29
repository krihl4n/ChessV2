package com.krihl4n.model

internal data class Piece(val color: Color, val type: Type)

internal enum class Color {
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

    override fun toString(): String = super.toString().lowercase()

    companion object {
        fun of(colorString: String): Color {
            return if (colorString.lowercase() == "white") WHITE
            else BLACK
        }
    }
}

internal enum class Type(val value: Int) {
    PAWN(1), KNIGHT(3), BISHOP(3), ROOK(5), QUEEN(9), KING(0);

    override fun toString(): String = super.toString().lowercase()

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
