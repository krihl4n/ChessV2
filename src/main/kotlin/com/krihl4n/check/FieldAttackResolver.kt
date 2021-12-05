package com.krihl4n.check

import com.krihl4n.PositionTracker
import com.krihl4n.model.Color
import com.krihl4n.model.Field
import com.krihl4n.moveCalculators.PieceMoveCalculator

class FieldAttackResolver(private val positionTracker: PositionTracker) {

    fun withPositionTracker(positionTracker: PositionTracker): FieldAttackResolver {
        return FieldAttackResolver(positionTracker)
    }

    fun isFieldUnderAttackByColor(field: Field, color: Color, pieceMoveCalculator: PieceMoveCalculator): Boolean {
        return positionTracker.getPositionsOfAllPieces()
            .filter { it.value.color == color }
            .flatMap { pieceMoveCalculator.findMoves(it.key) }
            .any { it.to == field }
    }
}