package com.krihl4n.moveCalculators

import com.krihl4n.game.guards.CastlingGuard
import com.krihl4n.game.guards.EnPassantGuard
import com.krihl4n.model.Type
import java.util.*

internal class CalculatorFactory {
    private var calculators: Map<Type, MoveCalculator>? = null

    fun getMoveCalculator(pieceType: Type): MoveCalculator {
        return calculators?.get(pieceType) ?: throw IllegalArgumentException("cannot find calculator")
    }

    fun initCalculators(enPassantGuard: EnPassantGuard, castlingGuard: CastlingGuard) {
        val calc = EnumMap<Type, MoveCalculator>(Type::class.java)
        calc[Type.ROOK] = RookMoveCalculator()
        calc[Type.PAWN] = PawnMoveCalculator(enPassantGuard)
        calc[Type.KNIGHT] = KnightMoveCalculator()
        calc[Type.BISHOP] = BishopMoveCalculator()
        calc[Type.QUEEN] = QueenMoveCalculator()
        calc[Type.KING] = KingMoveCalculator(castlingGuard)
        calculators = calc
    }
}
