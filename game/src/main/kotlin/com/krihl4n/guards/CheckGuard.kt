package com.krihl4n.guards

import com.krihl4n.PositionTracker
import com.krihl4n.model.*
import com.krihl4n.moveCalculators.PieceMoveCalculator
import com.krihl4n.moveCalculators.PossibleMove

internal class CheckGuard(private val positionTracker: PositionTracker) {

    fun isKingCheckedAfterMove(
        moveCalculator: PieceMoveCalculator,
        color: Color,
        possibleMove: PossibleMove,
    ): Boolean {
        val newTracker = positionTracker.copy()
        newTracker.movePiece(possibleMove.from, possibleMove.to)

        val kingPosition: Field = findKingPosition(color, newTracker) ?: return false
        return isFieldUnderAttackByColor(
            kingPosition,
            color.opposite(),
            moveCalculator.withPositionTracker(newTracker),
            newTracker)
    }

    private fun findKingPosition(color: Color, positionTracker: PositionTracker): Field? {
        return positionTracker.getPositionsOfAllPieces()
            .filter { it.value == Piece(color, Type.KING) }
            .map { it.key }
            .firstOrNull()
    }

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
