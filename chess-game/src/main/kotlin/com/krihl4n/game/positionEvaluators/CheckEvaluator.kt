package com.krihl4n.game.positionEvaluators

import com.krihl4n.PositionTracker
import com.krihl4n.model.*
import com.krihl4n.moveCalculators.PieceMoveCalculator
import com.krihl4n.moveCalculators.PossibleMove

internal class CheckEvaluator(private val positionTracker: PositionTracker, private val moveCalculator: PieceMoveCalculator) {

    fun isKingCheckedAfterMove(
        moveCalculator: PieceMoveCalculator = this.moveCalculator,
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

    fun isKingChecked(color: Color): Boolean {
        val kingPosition: Field = findKingPosition(color, this.positionTracker) ?: return false
        return isFieldUnderAttackByColor(
            kingPosition,
            color.opposite(),
            moveCalculator,
            this.positionTracker
        )
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
