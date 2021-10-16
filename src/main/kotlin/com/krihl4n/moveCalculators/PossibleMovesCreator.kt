package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.model.Field
import com.krihl4n.model.File
import com.krihl4n.model.Piece
import com.krihl4n.model.Rank

object PossibleMovesCreator {

    fun create(
        positionTracker: PositionTracker,
        start: Field,
        limit: Int? = null,
        nextFieldFunctions: Set<(Field) -> OptionalField>
    ): HashSet<PossibleMove> {
        val moves = HashSet<PossibleMove>()
        nextFieldFunctions.forEach {
            val piece = positionTracker.getPieceAt(start) ?: throw IllegalArgumentException("no piece at $start")
            var nextField = it.invoke(start)
            var fieldsAppended = 0
            while (nextField.isValid()) {
                if (limit != null && fieldsAppended == limit) {
                    break
                }
                val destination = Field(nextField.file!!, nextField.rank!!)
                if (positionTracker.isFieldOccupied(destination)) {
                    if (canAttackPiece(positionTracker.getPieceAt(destination), piece)) {
                        moves.add(PossibleMove(start, destination))
                    }
                    break
                } else {
                    moves.add(PossibleMove(start, destination))
                    nextField = it.invoke(destination)
                    fieldsAppended++
                }
            }
        }
        return moves
    }
}

data class OptionalField(val file: File?, val rank: Rank?) {
    fun isValid(): Boolean {
        return file != null && rank != null
    }
}

private fun canAttackPiece(pieceAtDestinationField: Piece?, piece: Piece) =
    pieceAtDestinationField?.color != piece.color
