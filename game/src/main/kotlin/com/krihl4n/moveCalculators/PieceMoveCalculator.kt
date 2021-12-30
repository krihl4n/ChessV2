package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.model.Field
import com.krihl4n.moveCalculators.CalculatorFactory.Companion.getMoveCalculator

internal class PieceMoveCalculator(
    private val positionTracker: PositionTracker
) {

    fun withPositionTracker(positionTracker: PositionTracker): PieceMoveCalculator {
        return PieceMoveCalculator(positionTracker)
    }

    fun findMoves(field: Field): Set<PossibleMove> {
        val piece = positionTracker.getPieceAt(field) ?: throw IllegalArgumentException("No piece at field $field")
        val calculator = getMoveCalculator(piece.type)
        return calculator.calculateMoves(field, positionTracker)
    }
}
