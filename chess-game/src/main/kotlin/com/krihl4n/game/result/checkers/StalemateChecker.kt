package com.krihl4n.game.result.checkers

import com.krihl4n.MoveValidator
import com.krihl4n.PositionTracker
import com.krihl4n.model.Color

internal class StalemateChecker(private val positionTracker: PositionTracker, private val moveValidator: MoveValidator) {

    fun occurs(color: Color) = noMoreValidMovesFor(color)

    private fun noMoreValidMovesFor(color: Color): Boolean {
        return positionTracker.getPositionsOfAllPieces()
            .filter { it.value.color == color }
            .map { moveValidator.getValidMoves(it.key, color) }
            .all { it.isEmpty() }
    }
}