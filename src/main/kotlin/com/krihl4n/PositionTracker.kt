package com.krihl4n

class PositionTracker {

    private val piecePositions: HashMap<Field, Piece> = HashMap()

    fun setPieceAtField(piece: Piece, field: Field) {
        piecePositions[field] = piece
    }

    fun getPieceAt(field: Field): Piece? {
        return piecePositions[field]
    }

    fun movePiece(start: Field, destination: Field) {
        if (start == destination)
            throw IllegalArgumentException("Start and destination fields are the same")

        val piece = piecePositions.remove(start) ?: throw IllegalArgumentException("No piece at field $start")
        piecePositions[destination] = piece
    }

    fun removePieceFromField(field: Field) {
        piecePositions.remove(field)
    }
}
