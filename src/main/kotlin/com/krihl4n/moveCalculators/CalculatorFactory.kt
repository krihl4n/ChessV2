package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.castling.CastlingGuard
import com.krihl4n.model.Type
import java.util.*

class CalculatorFactory(val positionTracker: PositionTracker, castlingGuard: CastlingGuard) {

    private val calculators: Map<Type, MoveCalculator>

    init {
        calculators = EnumMap(Type::class.java)
        calculators[Type.ROOK] = RookMoveCalculator(positionTracker)
        calculators[Type.PAWN] = PawnMoveCalculator(positionTracker)
        calculators[Type.KNIGHT] = KnightMoveCalculator(positionTracker)
        calculators[Type.BISHOP] = BishopMoveCalculator(positionTracker)
        calculators[Type.QUEEN] = QueenMoveCalculator(positionTracker)
        calculators[Type.KING] = KingMoveCalculator(positionTracker, castlingGuard)
    }

    fun getMoveCalculator(pieceType: Type): MoveCalculator {
        return calculators[pieceType] ?: throw IllegalArgumentException("cannot find calculator")
    }
}
