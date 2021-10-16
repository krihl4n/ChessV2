package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.model.Field

class QueenMoveCalculator(private val positionTracker: PositionTracker) : MoveCalculator {

    override fun calculateMoves(from: Field): Set<PossibleMove> {
        val possibleMoves = HashSet<PossibleMove>()

        possibleMoves.append(
            positionTracker,
            from,
            nextFieldFunctions = allDirectionsMoves
        )

        return possibleMoves
    }
}
