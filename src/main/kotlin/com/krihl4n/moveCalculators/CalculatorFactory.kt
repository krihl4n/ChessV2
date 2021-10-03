package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.model.Piece

class CalculatorFactory(val positionTracker: PositionTracker) {

    fun getMoveCalculator(piece: Piece): MoveCalculator {
        return PawnMoveCalculator(positionTracker) // todo map with instances?
    }
}
