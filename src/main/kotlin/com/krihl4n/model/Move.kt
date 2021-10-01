package com.krihl4n.model

import java.util.*

class Move(val piece: Piece, val from: Field, val to: Field) {

    val id = UUID.randomUUID()

    init {
        if (from == to)
            throw IllegalArgumentException("Move has to be performed to different location")
    }

    constructor(piece: Piece, from: String, to: String) : this(piece, Field(from), Field(to))

    companion object {
        @JvmStatic
        fun of(piece: Piece, expression: String): Move {
            val tokens = expression
                .split(" ")
            return Move(piece, tokens[0], tokens[1])
        }
    }
}
