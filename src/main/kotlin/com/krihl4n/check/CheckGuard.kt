package com.krihl4n.check

import com.krihl4n.PositionTracker
import com.krihl4n.command.MoveObserver
import com.krihl4n.model.*
import com.krihl4n.moveCalculators.PieceMoveCalculator
import com.krihl4n.moveCalculators.PossibleMove

class CheckGuard(val positionTracker: PositionTracker) :
    MoveObserver {

    override fun movePerformed(move: Move) {
        TODO("Not yet implemented")
    }

    override fun moveUndid(move: Move) {
        TODO("Not yet implemented")
    }

//    fun isKingChecked(color: Color): Boolean { // todo
//        val kingPosition: Field = findKingPosition(color) ?: return false
//        return fieldAttackResolver.isFieldUnderAttackByColor(kingPosition, color.opposite())
//    }

    fun willKingBeCheckedAfterMove(
        moveCalculator: PieceMoveCalculator,
        color: Color,
        possibleMove: PossibleMove,
    ): Boolean {
        val trackerWithMovePerformed = this.positionTracker.withMove(possibleMove.from, possibleMove.to)
        val kingPosition: Field = findKingPosition(color, trackerWithMovePerformed) ?: return false
        val result = isFieldUnderAttackByColor(
            kingPosition,
            color.opposite(),
            moveCalculator.withPositionTracker(trackerWithMovePerformed),
            trackerWithMovePerformed)

        if (result) {
            println("King would be checked if this move was performed!")
        }
        return result
    }

    fun findKingSavingMoves() {
        // todo ?
    }

    private fun findKingPosition(color: Color, positionTracker: PositionTracker): Field? {
        return positionTracker.getPositionsOfAllPieces()
            .filter { it.value == Piece(color, Type.KING) }
            .map { it.key }
            .firstOrNull()
    }

    private fun Color.opposite(): Color {
        return if (this == Color.WHITE) Color.BLACK else Color.WHITE
    }

    private fun isFieldUnderAttackByColor(
        field: Field,
        color: Color,
        pieceMoveCalculator: PieceMoveCalculator,
        positionTracker: PositionTracker,
    ): Boolean {
        return positionTracker.getPositionsOfAllPieces()
            .filter { it.value.color == color }
            .flatMap { pieceMoveCalculator.findMoves(it.key) }
            .any { it.to == field }
    }
}
