package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.model.Field

class QueenMoveCalculator: MoveCalculator {

    override fun calculateMoves(from: Field, positionTracker: PositionTracker): Set<PossibleMove> {
        return PossibleMovesCreator.create(
            positionTracker,
            from,
            nextFieldFunctions = allDirectionsMoves
        )
    }
}
