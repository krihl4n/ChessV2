package com.krihl4n.api.dto

import com.krihl4n.model.Field
import com.krihl4n.moveCalculators.PossibleMove

data class PossibleMovesDto(val from: String, val to: List<String>) {

    companion object {
        internal fun from(field: Field, possibleMoves: Set<PossibleMove>): PossibleMovesDto {
            return PossibleMovesDto(
                from = field.token(),
                to = possibleMoves.map { it.to.token() }
            )
        }

        internal fun noMovesFrom(field: Field): PossibleMovesDto {
            return PossibleMovesDto(
                from = field.token(),
                to = emptyList()
            )
        }
    }
}
