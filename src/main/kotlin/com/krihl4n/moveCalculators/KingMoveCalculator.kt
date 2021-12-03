package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.castling.CastlingGuard
import com.krihl4n.model.Field

class KingMoveCalculator(private val positionTracker: PositionTracker, private val castlingGuard: CastlingGuard) :
    MoveCalculator {

    override fun calculateMoves(from: Field): Set<PossibleMove> {
        val moves = PossibleMovesCreator.create(positionTracker, from, 1, allDirectionsMoves)
        if (from == Field("e1")) {
            if (castlingGuard.canWhiteKingLongCastle())
                moves.add(PossibleMove(Field("e1"), Field("c1")))
            if (castlingGuard.canWhiteKingShortCastle())
                moves.add(PossibleMove(Field("e1"), Field("g1")))
        }

        if (from == Field("e8")) {
            if (castlingGuard.canBlackKingLongCastle())
                moves.add(PossibleMove(Field("e8"), Field("c8")))
            if (castlingGuard.canBlackKingShortCastle())
                moves.add(PossibleMove(Field("e8"), Field("g8")))
        }

        return moves
    }
}
