package com.krihl4n.moveCalculators

import com.krihl4n.model.Type
import java.util.*

internal class CalculatorFactory {

    companion object {
        lateinit var calculators: Map<Type, MoveCalculator>

        fun getMoveCalculator(pieceType: Type): MoveCalculator {
            return calculators[pieceType] ?: throw IllegalArgumentException("cannot find calculator")
        }
    }

    init {
        val calc = EnumMap<Type, MoveCalculator>(Type::class.java)
        calc[Type.ROOK] = RookMoveCalculator()
        calc[Type.PAWN] = PawnMoveCalculator()
        calc[Type.KNIGHT] = KnightMoveCalculator()
        calc[Type.BISHOP] = BishopMoveCalculator()
        calc[Type.QUEEN] = QueenMoveCalculator()
        calc[Type.KING] = KingMoveCalculator()
        calculators = calc
    }
}
