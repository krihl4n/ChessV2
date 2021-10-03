package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.model.Field

class PieceMoveCalculator(
    private val positionTracker: PositionTracker,
    private val calculatorFactory: CalculatorFactory
) {

    fun findMoves(field: Field): Set<PossibleMove> {
        val piece = positionTracker.getPieceAt(field) ?: throw IllegalArgumentException("No piece at field $field")
        val calculator = calculatorFactory.getMoveCalculator(piece)
        return calculator.calculateMoves(field)
    }
}
