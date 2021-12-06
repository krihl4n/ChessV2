package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.model.Color
import com.krihl4n.model.Field
import com.krihl4n.moveCalculators.filters.PossibleMoveFilter

class PieceMoveCalculator(
    private val positionTracker: PositionTracker,
    private val calculatorFactory: CalculatorFactory,
    private val filters: Set<PossibleMoveFilter>,
) {

    fun withPositionTracker(positionTracker: PositionTracker): PieceMoveCalculator {
        return PieceMoveCalculator(positionTracker, calculatorFactory, filters)
    }

    fun findMoves(field: Field): Set<PossibleMove> {
        val piece = positionTracker.getPieceAt(field) ?: throw IllegalArgumentException("No piece at field $field")
        val calculator = calculatorFactory.withPositionTracker(positionTracker).getMoveCalculator(piece.type)
        val unfilteredMoves = calculator.calculateMoves(field)
        return filterMoves(piece.color, unfilteredMoves)
    }

    private fun filterMoves(color: Color, moves: Set<PossibleMove>): Set<PossibleMove> {
        var filteredMoves = moves
        filters.forEach { filter -> filteredMoves = HashSet(filter.filterMoves(this, color, filteredMoves)) }
        return filteredMoves
    }
}
