package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.model.Field

class RookMoveCalculator(positionTracker: PositionTracker) : MoveCalculator {

    override fun calculateMoves(from: Field): Set<PossibleMove> {
        val possibleMoves = HashSet<PossibleMove>()

        var nextFile = from.file + 1
        while (nextFile != null) {
            possibleMoves.add(PossibleMove(from, Field(nextFile, from.rank)))
            nextFile += 1
        }

        var previousFile = from.file - 1
        while(previousFile != null) {
            possibleMoves.add(PossibleMove(from, Field(previousFile, from.rank)))
            previousFile -= 1
        }

        return possibleMoves
    }
}
