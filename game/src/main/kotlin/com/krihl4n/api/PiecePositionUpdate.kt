package com.krihl4n.api

interface PiecePositionUpdate {

    fun pieceRemovedFromField(field: String)

    fun pieceSetOnField(piece: PieceDto, field: String)

    fun pieceMoved(from: String, to: String)
}