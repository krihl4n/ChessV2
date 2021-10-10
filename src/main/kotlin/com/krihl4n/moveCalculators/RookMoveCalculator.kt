package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.model.Field

class RookMoveCalculator(private val positionTracker: PositionTracker) : MoveCalculator {

    override fun calculateMoves(from: Field): Set<PossibleMove> {
        val possibleMoves = HashSet<PossibleMove>()

        possibleMoves.append(positionTracker, from) { field: Field -> OptionalField(field.file + 1, field.rank) }
        possibleMoves.append(positionTracker, from) { field: Field -> OptionalField(field.file - 1, field.rank) }
        possibleMoves.append(positionTracker, from) { field: Field -> OptionalField(field.file, field.rank + 1) }
        possibleMoves.append(positionTracker, from) { field: Field -> OptionalField(field.file, field.rank - 1) }
        return possibleMoves
    }
}
