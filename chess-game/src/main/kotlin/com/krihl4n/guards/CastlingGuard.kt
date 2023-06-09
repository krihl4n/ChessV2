package com.krihl4n.guards

import com.krihl4n.PositionTracker
import com.krihl4n.moveCommands.MoveObserver
import com.krihl4n.model.*
import com.krihl4n.moveCalculators.CalculatorFactory
import com.krihl4n.moveCalculators.PieceMoveCalculator

private const val WHITE_ROOK_KING_SIDE_STARTING_POS = "h1"
private const val WHITE_ROOK_QUEEN_SIDE_STARTING_POS = "a1"
private const val BLACK_ROOK_KING_SIDE_STARTING_POS = "h8"
private const val BLACK_ROOK_QUEEN_SIDE_STARTING_POS = "a8"
private const val WHITE_KING_STARTING_POS = "e1"
private const val BLACK_KING_STARTING_POS = "e8"

internal class CastlingGuard(
    private val positionTracker: PositionTracker,
    calculatorFactory: CalculatorFactory) : MoveObserver {

    private val whiteKingShortCastlingPermit = CastlingPermit()
    private val whiteKingLongCastlingPermit = CastlingPermit()
    private val blackKingShortCastlingPermit = CastlingPermit()
    private val blackKingLongCastlingPermit = CastlingPermit()

    private val pieceMoveCalculator = PieceMoveCalculator(positionTracker, calculatorFactory)

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
        return whiteKingShortCastlingPermit.isPermitted() &&
                !Color.BLACK.attacksAnyOfFields("e1", "f1")
    }

    fun canWhiteKingLongCastle(): Boolean {
        return whiteKingLongCastlingPermit.isPermitted() &&
                !Color.BLACK.attacksAnyOfFields("e1", "d1", "c1")
    }

    fun canBlackKingShortCastle(): Boolean {
        return blackKingShortCastlingPermit.isPermitted() &&
                !Color.WHITE.attacksAnyOfFields("e8", "f8")
    }

    fun canBlackKingLongCastle(): Boolean {
        return blackKingLongCastlingPermit.isPermitted() &&
                !Color.WHITE.attacksAnyOfFields("e8", "d8", "c8")
    }

    private fun Color.attacksAnyOfFields(vararg fields: String): Boolean {
        return fields.any { isFieldUnderAttackByColor(Field(it), this) }
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
    ): Boolean {
        return positionTracker.getPositionsOfAllPieces()
            .filter { it.value.color == color }
            .filterNot { it.value.type == Type.KING && it.key.rank.isFirstFor(it.value.color)} // ugly fix for bug_08_12_2022.txt (king cannot interrupt castling from the last rank)
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