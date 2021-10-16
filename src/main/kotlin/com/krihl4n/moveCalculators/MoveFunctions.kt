package com.krihl4n.moveCalculators

import com.krihl4n.model.Field

val verticalMoves = setOf(
    { field: Field -> OptionalField(field.file, field.rank + 1) },
    { field: Field -> OptionalField(field.file, field.rank - 1) }
)

val horizontalMoves = setOf(
    { field: Field -> OptionalField(field.file + 1, field.rank) },
    { field: Field -> OptionalField(field.file - 1, field.rank) }
)

val diagonalMoves = setOf(
    { field: Field -> OptionalField(field.file + 1, field.rank + 1) },
    { field: Field -> OptionalField(field.file - 1, field.rank + 1) },
    { field: Field -> OptionalField(field.file + 1, field.rank - 1) },
    { field: Field -> OptionalField(field.file - 1, field.rank - 1) }
)

val allDirectionsMoves = verticalMoves + horizontalMoves + diagonalMoves
