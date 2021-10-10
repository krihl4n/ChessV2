package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.model.Field
import com.krihl4n.model.File

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

        var nextRank = from.rank + 1
        while(nextRank != null) {
            possibleMoves.add(PossibleMove(from, Field(from.file, nextRank)))
            nextRank += 1
        }

        var previousRank = from.rank + 1
        while(previousRank != null) {
            possibleMoves.add(PossibleMove(from, Field(from.file, previousRank)))
            previousRank -= 1
        }

        return possibleMoves
    }
}
