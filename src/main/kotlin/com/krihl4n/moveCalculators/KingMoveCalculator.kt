package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.model.Field

class KingMoveCalculator(private val positionTracker: PositionTracker) : MoveCalculator {

    override fun calculateMoves(from: Field): Set<PossibleMove> {
        val moves = PossibleMovesCreator.create(positionTracker, from, 1, allDirectionsMoves)
        // todo can castle check
        if (from == Field("e1")) {
            moves.add(PossibleMove(Field("e1"), Field("c1")))
            moves.add(PossibleMove(Field("e1"), Field("g1")))
        }

        if (from == Field("e8")) {
            moves.add(PossibleMove(Field("e8"), Field("c8")))
            moves.add(PossibleMove(Field("e8"), Field("g8")))
        }

        return moves
    }
}
