package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.model.Field

internal class KnightMoveCalculator : MoveCalculator {

    override fun calculateMoves(from: Field, positionTracker: PositionTracker): Set<PossibleMove> {
        return PossibleMovesCreator.create(positionTracker, from, 1, setOf(
            { field: Field -> OptionalField(field.file + 1, field.rank + 2) },
            { field: Field -> OptionalField(field.file + 2, field.rank + 1) },
            { field: Field -> OptionalField(field.file - 1, field.rank + 2) },
            { field: Field -> OptionalField(field.file - 2, field.rank + 1) },
            { field: Field -> OptionalField(field.file + 1, field.rank - 2) },
            { field: Field -> OptionalField(field.file + 2, field.rank - 1) },
            { field: Field -> OptionalField(field.file - 1, field.rank - 2) },
            { field: Field -> OptionalField(field.file - 2, field.rank - 1) }
        ))
    }
}
