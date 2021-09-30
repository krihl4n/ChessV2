package com.krihl4n

class PositionTracker {

    private val piecePositions: HashMap<Field, Piece> = HashMap()

    fun set(piece: Piece, field: Field) {
        piecePositions[field] = piece
    }

    fun getPieceAt(field: Field): Piece? {
        return piecePositions[field]
    }
}
