package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.model.Piece
import com.krihl4n.model.Type

class CalculatorFactory(val positionTracker: PositionTracker) {

    fun getMoveCalculator(piece: Piece): MoveCalculator {
        return when (piece.type) {
            Type.ROOK -> RookMoveCalculator(positionTracker)
            Type.PAWN -> PawnMoveCalculator(positionTracker)
            Type.KNIGHT -> TODO()
            Type.BISHOP -> TODO()
            Type.QUEEN -> TODO()
            Type.KING -> KingMoveCalculator(positionTracker)
        }
        // todo map with instances?
    }
}
