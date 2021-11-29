package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.model.Field

class KingMoveCalculator(private val positionTracker: PositionTracker) : MoveCalculator {

    override fun calculateMoves(from: Field): Set<PossibleMove> {
        val moves = PossibleMovesCreator.create(positionTracker, from, 1, allDirectionsMoves)
        if (from == Field("e1")) {
            moves.add(PossibleMove(Field("e1"), Field("c1"))) // todo can castle
        }
        return moves
    }
}
