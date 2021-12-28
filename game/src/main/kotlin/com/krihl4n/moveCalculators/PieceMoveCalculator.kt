package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.model.Color
import com.krihl4n.model.Field
import com.krihl4n.moveCalculators.CalculatorFactory.Companion.getMoveCalculator
import com.krihl4n.moveCalculators.filters.OwnKingCannotBeCheckedAfterMoveFilter
import com.krihl4n.moveCalculators.filters.PossibleMoveFilter

internal class PieceMoveCalculator(
    private val positionTracker: PositionTracker
) {

    private val filters: Set<PossibleMoveFilter> = setOf(OwnKingCannotBeCheckedAfterMoveFilter())

    fun withPositionTracker(positionTracker: PositionTracker): PieceMoveCalculator {
        return PieceMoveCalculator(positionTracker)
    }

    fun findMoves(field: Field): Set<PossibleMove> {
        val piece = positionTracker.getPieceAt(field) ?: throw IllegalArgumentException("No piece at field $field")
        val calculator = getMoveCalculator(piece.type)
        val unfilteredMoves = calculator.calculateMoves(field, positionTracker)
        return filterMoves(piece.color, unfilteredMoves)
    }

    private fun filterMoves(color: Color, moves: Set<PossibleMove>): Set<PossibleMove> {
        var filteredMoves = moves
        filters.forEach { filter -> filteredMoves = HashSet(filter.filterMoves(this, color, filteredMoves)) }
        return filteredMoves
    }
}
