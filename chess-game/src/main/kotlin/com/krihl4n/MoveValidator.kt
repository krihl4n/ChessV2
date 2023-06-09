package com.krihl4n

import com.krihl4n.guards.CheckEvaluator
import com.krihl4n.model.Color
import com.krihl4n.model.Field
import com.krihl4n.model.Move
import com.krihl4n.moveCalculators.PieceMoveCalculator
import com.krihl4n.moveCalculators.PossibleMove

internal class MoveValidator(private val pieceMoveCalculator: PieceMoveCalculator, private val checkEvaluator: CheckEvaluator) {

    fun isMoveValid(move: Move) : Boolean {
        return getValidMoves(move.from, move.piece.color)
            .stream()
            .anyMatch { it.from == move.from && it.to == move.to }
    }

    fun getValidMoves(field: Field, color: Color): Set<PossibleMove> {
        val possibleMoves = pieceMoveCalculator.findMoves(field)
        return filterMoves(color, possibleMoves)
    }

    private fun filterMoves(color: Color, moves: Set<PossibleMove>): Set<PossibleMove> {
        return filterMoves(pieceMoveCalculator, color, moves)
    }

    private fun filterMoves(
        moveCalculator: PieceMoveCalculator,
        movingColor: Color,
        possibleMoves: Set<PossibleMove>,
    ): Set<PossibleMove> {
        return possibleMoves.filter { !checkEvaluator.isKingCheckedAfterMove(moveCalculator, movingColor, it) }.toSet()
    }
}
