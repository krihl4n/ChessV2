package com.krihl4n.castling

import com.krihl4n.PositionTracker
import com.krihl4n.command.MoveObserver
import com.krihl4n.model.*
import com.krihl4n.moveCalculators.PieceMoveCalculator

private const val WHITE_ROOK_KING_SIDE_STARTING_POS = "h1"
private const val WHITE_ROOK_QUEEN_SIDE_STARTING_POS = "a1"
private const val BLACK_ROOK_KING_SIDE_STARTING_POS = "h8"
private const val BLACK_ROOK_QUEEN_SIDE_STARTING_POS = "a8"
private const val WHITE_KING_STARTING_POS = "e1"
private const val BLACK_KING_STARTING_POS = "e8"

class CastlingGuard : MoveObserver {

    private val whiteKingShortCastlingPermit = CastlingPermit()
    private val whiteKingLongCastlingPermit = CastlingPermit()
    private val blackKingShortCastlingPermit = CastlingPermit()
    private val blackKingLongCastlingPermit = CastlingPermit()

    override fun movePerformed(move: Move) {
        if (move.whiteKingMovedFromStartingPosition()) {
            whiteKingShortCastlingPermit.blockCastling(move)
            whiteKingLongCastlingPermit.blockCastling(move)
        }

        if (move.blackKingMovedFromStartingPosition()) {
            blackKingShortCastlingPermit.blockCastling(move)
            blackKingLongCastlingPermit.blockCastling(move)
        }

        if (move.whiteRookMovedFrom(WHITE_ROOK_KING_SIDE_STARTING_POS)) {
            whiteKingShortCastlingPermit.blockCastling(move)
        }

        if (move.whiteRookMovedFrom(WHITE_ROOK_QUEEN_SIDE_STARTING_POS)) {
            whiteKingLongCastlingPermit.blockCastling(move)
        }

        if (move.blackRookMovedFrom(BLACK_ROOK_KING_SIDE_STARTING_POS)) {
            blackKingShortCastlingPermit.blockCastling(move)
        }

        if (move.blackRookMovedFrom(BLACK_ROOK_QUEEN_SIDE_STARTING_POS)) {
            blackKingLongCastlingPermit.blockCastling(move)
        }
    }

    override fun moveUndid(move: Move) {
        if (move.whiteKingMovedFromStartingPosition()) {
            whiteKingShortCastlingPermit.unblockCastling(move)
            whiteKingLongCastlingPermit.unblockCastling(move)
        }

        if (move.blackKingMovedFromStartingPosition()) {
            blackKingShortCastlingPermit.unblockCastling(move)
            blackKingLongCastlingPermit.unblockCastling(move)
        }

        if (move.whiteRookMovedFrom(WHITE_ROOK_KING_SIDE_STARTING_POS)) {
            whiteKingShortCastlingPermit.unblockCastling(move)
        }

        if (move.whiteRookMovedFrom(WHITE_ROOK_QUEEN_SIDE_STARTING_POS)) {
            whiteKingLongCastlingPermit.unblockCastling(move)
        }

        if (move.blackRookMovedFrom(BLACK_ROOK_KING_SIDE_STARTING_POS)) {
            blackKingShortCastlingPermit.unblockCastling(move)
        }

        if (move.blackRookMovedFrom(BLACK_ROOK_QUEEN_SIDE_STARTING_POS)) {
            blackKingLongCastlingPermit.unblockCastling(move)
        }
    }

    fun canWhiteKingShortCastle(): Boolean {
        return whiteKingShortCastlingPermit.isPermitted()
    }

    fun canWhiteKingLongCastle(): Boolean {
        return whiteKingLongCastlingPermit.isPermitted()
    }

    fun canBlackKingShortCastle(): Boolean {
        return blackKingShortCastlingPermit.isPermitted()
    }

    fun canBlackKingLongCastle(): Boolean {
        return blackKingLongCastlingPermit.isPermitted()
    }

    private fun Move.whiteRookMovedFrom(field: String) =
        this.piece == Piece(Color.WHITE, Type.ROOK) && this.from == Field(field)

    private fun Move.blackRookMovedFrom(field: String) =
        this.piece == Piece(Color.BLACK, Type.ROOK) && this.from == Field(field)

    private fun Move.whiteKingMovedFromStartingPosition() =
        this.piece == Piece(Color.WHITE, Type.KING) &&
                this.from == Field(WHITE_KING_STARTING_POS)

    private fun Move.blackKingMovedFromStartingPosition() =
        this.piece == Piece(Color.BLACK, Type.KING) &&
                this.from == Field(BLACK_KING_STARTING_POS)

    private fun isFieldUnderAttackByColor(
        field: Field,
        color: Color,
        pieceMoveCalculator: PieceMoveCalculator,
        positionTracker: PositionTracker,
    ): Boolean {
        return positionTracker.getPositionsOfAllPieces()
            .filter { it.value.color == color }
            .flatMap { pieceMoveCalculator.findMoves(it.key) }
            .any { it.to == field }
    }
}

private class CastlingPermit {
    val blockingMoves = mutableSetOf<Move>()

    fun blockCastling(move: Move) {
        if (blockingMoves.alreadyBlockedByMoveFromField(move.from))
            return
        blockingMoves.add(move)
    }

    fun unblockCastling(move: Move) {
        blockingMoves.remove(move)
    }

    fun isPermitted(): Boolean {
        return blockingMoves.isEmpty()
    }

    fun Set<Move>.alreadyBlockedByMoveFromField(field: Field) = this.find { it.from == field } != null
}