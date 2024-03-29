package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.game.guards.CastlingGuard
import com.krihl4n.model.Field

internal class KingMoveCalculator(private val castlingGuard: CastlingGuard) : MoveCalculator {

    override fun calculateMoves(from: Field, positionTracker: PositionTracker): Set<PossibleMove> {
        val moves = PossibleMovesCreator.create(positionTracker, from, 1, allDirectionsMoves)
        if (from == Field("e1")) {
            if (positionTracker.fieldsAreEmpty("b1", "c1", "d1") && castlingGuard.canWhiteKingLongCastle())
                moves.add(PossibleMove(Field("e1"), Field("c1")))
            if (positionTracker.fieldsAreEmpty("f1", "g1") && castlingGuard.canWhiteKingShortCastle())
                moves.add(PossibleMove(Field("e1"), Field("g1")))
        }

        if (from == Field("e8")) {
            if (positionTracker.fieldsAreEmpty("b8", "c8", "d8") && castlingGuard.canBlackKingLongCastle())
                moves.add(PossibleMove(Field("e8"), Field("c8")))
            if (positionTracker.fieldsAreEmpty("f8", "g8") && castlingGuard.canBlackKingShortCastle())
                moves.add(PossibleMove(Field("e8"), Field("g8")))
        }

        return moves
    }

    private fun PositionTracker.fieldsAreEmpty(vararg fields: String): Boolean {
        fields.forEach {
            if (!isFieldEmpty(Field(it)))
                return false
        }
        return true
    }
}
