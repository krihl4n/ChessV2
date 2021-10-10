package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.model.Field
import com.krihl4n.model.File
import com.krihl4n.model.Piece
import com.krihl4n.model.Rank

class RookMoveCalculator(private val positionTracker: PositionTracker) : MoveCalculator {

    override fun calculateMoves(from: Field): Set<PossibleMove> {
        val possibleMoves = HashSet<PossibleMove>()
        val rook = positionTracker.getPieceAt(from) ?: throw IllegalArgumentException("no piece at $from")

        possibleMoves.append(rook, from) { field: Field -> OptionalField(field.file + 1, field.rank) }
        possibleMoves.append(rook, from) { field: Field -> OptionalField(field.file - 1, field.rank) }
        possibleMoves.append(rook, from) { field: Field -> OptionalField(field.file, field.rank + 1) }
        possibleMoves.append(rook, from) { field: Field -> OptionalField(field.file, field.rank - 1) }
        return possibleMoves
    }

    data class OptionalField(val file: File?, val rank: Rank?) {
        fun isValid(): Boolean {
            return file != null && rank != null
        }
    }

    private fun HashSet<PossibleMove>.append(
        piece: Piece,
        start: Field,
        getNextField: (Field) -> OptionalField
    ) {
        var nextField = getNextField.invoke(start)

        while (nextField.isValid()) {
            val destination = Field(nextField.file!!, nextField.rank!!)
            if (positionTracker.isFieldOccupied(destination)) {
                if (canAttackOccupiedField(destination, piece)) {
                    this.add(PossibleMove(start, destination))
                }
                break
            } else {
                this.add(PossibleMove(start, destination))
                nextField = getNextField.invoke(destination)
            }
        }
    }

    private fun canAttackOccupiedField(destination: Field, rook: Piece) =
        positionTracker.getPieceAt(destination)?.color != rook.color
}
