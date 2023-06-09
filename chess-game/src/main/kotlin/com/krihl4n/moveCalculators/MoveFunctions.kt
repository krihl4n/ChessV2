package com.krihl4n.moveCalculators

import com.krihl4n.model.Field

internal val verticalMoves = setOf(
    { field: Field -> OptionalField(field.file, field.rank + 1) },
    { field: Field -> OptionalField(field.file, field.rank - 1) }
)

internal val horizontalMoves = setOf(
    { field: Field -> OptionalField(field.file + 1, field.rank) },
    { field: Field -> OptionalField(field.file - 1, field.rank) }
)

internal val diagonalMoves = setOf(
    { field: Field -> OptionalField(field.file + 1, field.rank + 1) },
    { field: Field -> OptionalField(field.file - 1, field.rank + 1) },
    { field: Field -> OptionalField(field.file + 1, field.rank - 1) },
    { field: Field -> OptionalField(field.file - 1, field.rank - 1) }
)

internal val allDirectionsMoves = verticalMoves + horizontalMoves + diagonalMoves
