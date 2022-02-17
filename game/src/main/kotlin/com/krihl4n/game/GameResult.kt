package com.krihl4n.game

import com.krihl4n.PositionTracker
import com.krihl4n.model.*
import com.krihl4n.model.Field
import com.krihl4n.model.Move
import com.krihl4n.model.Piece
import com.krihl4n.moveCalculators.PieceMoveCalculator
import com.krihl4n.moveCommands.MoveObserver

internal class GameResult(val positionTracker: PositionTracker, val moveCalculator: PieceMoveCalculator) :
    MoveObserver {

    private val resultObservers = mutableListOf<GameResultObserver>()

    override fun movePerformed(move: Move) {
        if (isKingChecked(move.piece.color.opposite())) {
            // check if mate, not just check
            notifyGameFinished()
        }
    }

    override fun moveUndid(move: Move) {
        // todo
    }

    fun isGameFinished(): Boolean {  // maybe not needed
        return false
    }

    fun registerObserver(observer: GameResultObserver) {
        resultObservers.add(observer)
    }

    private fun notifyGameFinished() {
        resultObservers.forEach { it.gameFinished() }
    }

    private fun isKingChecked(color: Color): Boolean {
        val kingPosition: Field = findKingPosition(color) ?: return false
        return isFieldUnderAttackByColor(
            kingPosition,
            color.opposite()
        )
    }

    private fun findKingPosition(color: Color): Field? {
        return positionTracker.getPositionsOfAllPieces()
            .filter { it.value == Piece(color, Type.KING) }
            .map { it.key }
            .firstOrNull()
    }

    private fun isFieldUnderAttackByColor(
        field: Field,
        color: Color
    ): Boolean {
        return positionTracker.getPositionsOfAllPieces()
            .filter { it.value.color == color }
            .flatMap { moveCalculator.findMoves(it.key) }
            .any { it.to == field }
    }
}
