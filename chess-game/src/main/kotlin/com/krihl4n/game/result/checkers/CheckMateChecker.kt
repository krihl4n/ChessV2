package com.krihl4n.game.result.checkers

import com.krihl4n.MoveValidator
import com.krihl4n.PositionTracker
import com.krihl4n.guards.CheckEvaluator
import com.krihl4n.model.Color

internal class CheckMateChecker(private val positionTracker: PositionTracker, private val checkEvaluator: CheckEvaluator, private val moveValidator: MoveValidator) {

    fun occurs(color: Color): Boolean {
        return checkEvaluator.isKingChecked(color) && !isThereASavingMove(color)
    }

    private fun isThereASavingMove(color: Color): Boolean {
        return positionTracker.getPositionsOfAllPieces()
            .filter { it.value.color == color }
            .flatMap { moveValidator.getValidMoves(it.key, color) }
            .firstOrNull {
                !checkEvaluator.isKingCheckedAfterMove(
                    color = color,
                    possibleMove = it
                )
            } != null
    }
}