package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.model.Piece
import com.krihl4n.model.Type

class CalculatorFactory(val positionTracker: PositionTracker) {

    fun getMoveCalculator(piece: Piece): MoveCalculator {
        return when (piece.type) {
            Type.ROOK -> RookMoveCalculator(positionTracker)
            Type.PAWN -> PawnMoveCalculator(positionTracker)
            Type.KNIGHT -> KnightMoveCalculator(positionTracker)
            Type.BISHOP -> BishopMoveCalculator(positionTracker)
            Type.QUEEN -> QueenMoveCalculator(positionTracker)
            Type.KING -> KingMoveCalculator(positionTracker)
        }
        // todo map with instances?®
    }
}
