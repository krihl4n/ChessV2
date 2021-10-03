package com.krihl4n.moveCalculators

import com.krihl4n.model.Piece

object CalculatorFactory {

    fun getMoveCalculator(piece: Piece): MoveCalculator {
        return PawnMoveCalculator
    }
}
