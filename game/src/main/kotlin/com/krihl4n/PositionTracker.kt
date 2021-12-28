package com.krihl4n

import com.krihl4n.model.Field
import com.krihl4n.model.Piece
import kotlin.collections.HashMap

internal class PositionTracker(private val piecePositions: HashMap<Field, Piece> = HashMap()) {

    fun withMove(start: Field, destination: Field): PositionTracker {
        val newTracker = PositionTracker(HashMap(this.piecePositions))
        newTracker.movePiece(start, destination)
        return newTracker
    }

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

    fun isFieldEmpty(field: Field): Boolean {
        return piecePositions[field] == null
    }

    fun isFieldOccupied(field: Field): Boolean {
        return !isFieldEmpty(field)
    }

    fun getPositionsOfAllPieces(): HashMap<Field, Piece> {
        return HashMap(piecePositions)
    }
}
