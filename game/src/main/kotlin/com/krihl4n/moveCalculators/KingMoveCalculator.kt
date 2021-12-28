package com.krihl4n.moveCalculators

import com.krihl4n.Dependencies.Companion.castlingGuard
import com.krihl4n.PositionTracker
import com.krihl4n.model.Field

internal class KingMoveCalculator : MoveCalculator {

    override fun calculateMoves(from: Field, positionTracker: PositionTracker): Set<PossibleMove> {
        val moves = PossibleMovesCreator.create(positionTracker, from, 1, allDirectionsMoves)
        if (from == Field("e1")) {
            if (castlingGuard.canWhiteKingLongCastle() && positionTracker.fieldsAreEmpty("b1", "c1", "d1"))
                moves.add(PossibleMove(Field("e1"), Field("c1")))
            if (castlingGuard.canWhiteKingShortCastle() && positionTracker.fieldsAreEmpty("f1", "g1"))
                moves.add(PossibleMove(Field("e1"), Field("g1")))
        }

        if (from == Field("e8")) {
            if (castlingGuard.canBlackKingLongCastle() && positionTracker.fieldsAreEmpty("b8", "c8", "d8"))
                moves.add(PossibleMove(Field("e8"), Field("c8")))
            if (castlingGuard.canBlackKingShortCastle() && positionTracker.fieldsAreEmpty("f8", "g8"))
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
