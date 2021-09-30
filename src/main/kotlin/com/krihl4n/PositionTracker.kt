package com.krihl4n

class PositionTracker {

    private val piecePositions: HashMap<Field, Piece> = HashMap()

    fun set(piece: Piece, field: Field) {
        piecePositions[field] = piece
    }

    fun getPieceAt(field: Field): Piece? {
        return piecePositions[field]
    }

    fun move(start: Field, destination: Field) {
        if (start == destination)
            throw IllegalArgumentException("Start and destination fields are the same")

        val piece = piecePositions.remove(start) ?: throw IllegalArgumentException("No piece at field $start")
        piecePositions[destination] = piece
    }
}
