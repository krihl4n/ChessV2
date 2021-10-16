package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.model.Field

class RookMoveCalculator(private val positionTracker: PositionTracker) : MoveCalculator {

    override fun calculateMoves(from: Field): Set<PossibleMove> {
        return PossibleMovesCreator.create(positionTracker, from, nextFieldFunctions = verticalMoves + horizontalMoves)
    }
}
