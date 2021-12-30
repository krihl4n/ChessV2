package com.krihl4n.api

import com.krihl4n.model.Piece

data class PieceDto(
    val color: String, val type: String,
) {
    companion object {
        internal fun from(piece: Piece): PieceDto {
            return PieceDto(piece.color.toString(), piece.type.toString())
        }
    }
}