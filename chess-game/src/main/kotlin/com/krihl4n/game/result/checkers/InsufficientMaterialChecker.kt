package com.krihl4n.game.result.checkers

import com.krihl4n.PositionTracker
import com.krihl4n.model.Color
import com.krihl4n.model.Piece
import com.krihl4n.model.Type

internal class InsufficientMaterialChecker(private val positionTracker: PositionTracker) {

    fun occurs(): Boolean {
        val piecesLeft = positionTracker.getPositionsOfAllPieces().map { it.value }

        return piecesLeft
            .hasPieces(
                Piece(Color.WHITE, Type.KING),
                Piece(Color.BLACK, Type.KING)
            ) ||
                piecesLeft.hasPieces(
                    Piece(Color.WHITE, Type.BISHOP),
                    Piece(Color.WHITE, Type.KING),
                    Piece(Color.BLACK, Type.KING)
                ) ||
                piecesLeft.hasPieces(
                    Piece(Color.BLACK, Type.BISHOP),
                    Piece(Color.WHITE, Type.KING),
                    Piece(Color.BLACK, Type.KING)
                ) ||
                piecesLeft.hasPieces(
                    Piece(Color.WHITE, Type.KNIGHT),
                    Piece(Color.WHITE, Type.KING),
                    Piece(Color.BLACK, Type.KING)
                ) ||
                piecesLeft.hasPieces(
                    Piece(Color.BLACK, Type.KNIGHT),
                    Piece(Color.WHITE, Type.KING),
                    Piece(Color.BLACK, Type.KING)
                ) ||
                piecesLeft.hasPieces(
                    Piece(Color.WHITE, Type.BISHOP),
                    Piece(Color.BLACK, Type.BISHOP),
                    Piece(Color.WHITE, Type.KING),
                    Piece(Color.BLACK, Type.KING)
                ) ||
                piecesLeft.hasPieces(
                    Piece(Color.WHITE, Type.KNIGHT),
                    Piece(Color.BLACK, Type.KNIGHT),
                    Piece(Color.WHITE, Type.KING),
                    Piece(Color.BLACK, Type.KING)
                ) ||
                piecesLeft.hasPieces(
                    Piece(Color.WHITE, Type.KNIGHT),
                    Piece(Color.BLACK, Type.BISHOP),
                    Piece(Color.WHITE, Type.KING),
                    Piece(Color.BLACK, Type.KING)
                ) ||
                piecesLeft.hasPieces(
                    Piece(Color.BLACK, Type.KNIGHT),
                    Piece(Color.WHITE, Type.BISHOP),
                    Piece(Color.WHITE, Type.KING),
                    Piece(Color.BLACK, Type.KING)
                ) ||
                piecesLeft.hasPieces(
                    Piece(Color.BLACK, Type.KNIGHT),
                    Piece(Color.BLACK, Type.KNIGHT),
                    Piece(Color.WHITE, Type.KING),
                    Piece(Color.BLACK, Type.KING)
                ) ||
                piecesLeft.hasPieces(
                    Piece(Color.WHITE, Type.KNIGHT),
                    Piece(Color.WHITE, Type.KNIGHT),
                    Piece(Color.WHITE, Type.KING),
                    Piece(Color.BLACK, Type.KING)
                )
    }

    private fun List<Piece>.hasPieces(vararg pieces: Piece): Boolean {
        if (this.size != pieces.size) return false
        val piecesList = this.toMutableList()
        for (piece in pieces) {
            val index = piecesList.indexOfFirst { it == piece }
            if (index == -1) return false
            piecesList.removeAt(index)
        }
        return piecesList.size == 0
    }
}