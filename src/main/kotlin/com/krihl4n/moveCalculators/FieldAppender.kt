package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.model.Field
import com.krihl4n.model.File
import com.krihl4n.model.Piece
import com.krihl4n.model.Rank

fun HashSet<PossibleMove>.append(
    positionTracker: PositionTracker,
    start: Field,
    getNextField: (Field) -> OptionalField
) {
    val piece = positionTracker.getPieceAt(start) ?: throw IllegalArgumentException("no piece at $start")
    var nextField = getNextField.invoke(start)

    while (nextField.isValid()) {
        val destination = Field(nextField.file!!, nextField.rank!!)
        if (positionTracker.isFieldOccupied(destination)) {
            if (canAttackPiece(positionTracker.getPieceAt(destination), piece)) {
                this.add(PossibleMove(start, destination))
            }
            break
        } else {
            this.add(PossibleMove(start, destination))
            nextField = getNextField.invoke(destination)
        }
    }
}

data class OptionalField(val file: File?, val rank: Rank?) {
    fun isValid(): Boolean {
        return file != null && rank != null
    }
}

private fun canAttackPiece(pieceAtDestinationField: Piece?, piece: Piece) =
    pieceAtDestinationField?.color != piece.color

