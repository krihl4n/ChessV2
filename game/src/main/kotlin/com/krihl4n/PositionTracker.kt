package com.krihl4n

import com.krihl4n.model.Color
import com.krihl4n.model.Field
import com.krihl4n.model.Piece
import com.krihl4n.model.Type
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

    fun resetInitialGameSetup() {
        piecePositions.clear()

        piecePositions[Field("a1")] = Piece(Color.WHITE, Type.ROOK)
        piecePositions[Field("b1")] = Piece(Color.WHITE, Type.KNIGHT)
        piecePositions[Field("c1")] = Piece(Color.WHITE, Type.BISHOP)
        piecePositions[Field("d1")] = Piece(Color.WHITE, Type.QUEEN)
        piecePositions[Field("e1")] = Piece(Color.WHITE, Type.KING)
        piecePositions[Field("f1")] = Piece(Color.WHITE, Type.BISHOP)
        piecePositions[Field("g1")] = Piece(Color.WHITE, Type.KNIGHT)
        piecePositions[Field("h1")] = Piece(Color.WHITE, Type.ROOK)

        piecePositions[Field("a2")] = Piece(Color.WHITE, Type.PAWN)
        piecePositions[Field("b2")] = Piece(Color.WHITE, Type.PAWN)
        piecePositions[Field("c2")] = Piece(Color.WHITE, Type.PAWN)
        piecePositions[Field("d2")] = Piece(Color.WHITE, Type.PAWN)
        piecePositions[Field("e2")] = Piece(Color.WHITE, Type.PAWN)
        piecePositions[Field("f2")] = Piece(Color.WHITE, Type.PAWN)
        piecePositions[Field("g2")] = Piece(Color.WHITE, Type.PAWN)
        piecePositions[Field("h2")] = Piece(Color.WHITE, Type.PAWN)

        piecePositions[Field("a8")] = Piece(Color.BLACK, Type.ROOK)
        piecePositions[Field("b8")] = Piece(Color.BLACK, Type.KNIGHT)
        piecePositions[Field("c8")] = Piece(Color.BLACK, Type.BISHOP)
        piecePositions[Field("d8")] = Piece(Color.BLACK, Type.QUEEN)
        piecePositions[Field("e8")] = Piece(Color.BLACK, Type.KING)
        piecePositions[Field("f8")] = Piece(Color.BLACK, Type.BISHOP)
        piecePositions[Field("g8")] = Piece(Color.BLACK, Type.KNIGHT)
        piecePositions[Field("h8")] = Piece(Color.BLACK, Type.ROOK)

        piecePositions[Field("a7")] = Piece(Color.BLACK, Type.PAWN)
        piecePositions[Field("b7")] = Piece(Color.BLACK, Type.PAWN)
        piecePositions[Field("c7")] = Piece(Color.BLACK, Type.PAWN)
        piecePositions[Field("d7")] = Piece(Color.BLACK, Type.PAWN)
        piecePositions[Field("e7")] = Piece(Color.BLACK, Type.PAWN)
        piecePositions[Field("f7")] = Piece(Color.BLACK, Type.PAWN)
        piecePositions[Field("g7")] = Piece(Color.BLACK, Type.PAWN)
        piecePositions[Field("h7")] = Piece(Color.BLACK, Type.PAWN)
    }
}
