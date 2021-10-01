package com.krihl4n.model

import java.util.*

class Move(val piece: Piece, val from: Field, val to: Field) {

    val id = UUID.randomUUID()

    init {
        if (from == to)
            throw IllegalArgumentException("Move has to be performed to different location")
    }
}
