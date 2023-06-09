package com.krihl4n.model

import java.util.*

internal class Move(val piece: Piece, val from: Field, val to: Field, val pawnPromotion: Type? = null) {

    private val id = UUID.randomUUID()

    init {
        if (from == to)
            throw IllegalArgumentException("Move has to be performed to different location")
    }

    constructor(piece: Piece, from: String, to: String, pawnPromotion: String?) : this(piece, Field(from), Field(to), pawnPromotion?.let { Type.of(it) })

    override fun toString(): String {
        return "Move(piece=$piece, from=$from, to=$to)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Move

        if (piece != other.piece) return false
        if (from != other.from) return false
        if (to != other.to) return false
        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        var result = piece.hashCode()
        result = 31 * result + from.hashCode()
        result = 31 * result + to.hashCode()
        result = 31 * result + (id?.hashCode() ?: 0)
        return result
    }
}
