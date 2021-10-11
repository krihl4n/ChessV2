package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.model.Field

class QueenMoveCalculator(private val positionTracker: PositionTracker) : MoveCalculator {

    override fun calculateMoves(from: Field): Set<PossibleMove> {
        val possibleMoves = HashSet<PossibleMove>()

        possibleMoves.append(positionTracker, from, nextFieldFunctions = setOf(
            { field: Field -> OptionalField(field.file + 1, field.rank) },
            { field: Field -> OptionalField(field.file - 1, field.rank) },
            { field: Field -> OptionalField(field.file, field.rank + 1) },
            { field: Field -> OptionalField(field.file, field.rank - 1) },
            { field: Field -> OptionalField(field.file + 1, field.rank + 1) },
            { field: Field -> OptionalField(field.file - 1, field.rank + 1) },
            { field: Field -> OptionalField(field.file + 1, field.rank - 1) },
            { field: Field -> OptionalField(field.file - 1, field.rank - 1) }
        ))

        return possibleMoves
    }
}
