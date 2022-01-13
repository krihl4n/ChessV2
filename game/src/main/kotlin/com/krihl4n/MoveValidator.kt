package com.krihl4n
import com.krihl4n.guards.CheckGuard
import com.krihl4n.model.Color
import com.krihl4n.model.Move
import com.krihl4n.moveCalculators.PieceMoveCalculator
import com.krihl4n.moveCalculators.PossibleMove

internal class MoveValidator(private val pieceMoveCalculator: PieceMoveCalculator, private val checkGuard: CheckGuard) {

    fun isMoveValid(move: Move) : Boolean {
        val possibleMoves = pieceMoveCalculator.findMoves(move.from)
        val filteredMoves = filterMoves(move.piece.color, possibleMoves)

        return filteredMoves.stream().anyMatch { it.from == move.from && it.to == move.to }
    }

    private fun filterMoves(color: Color, moves: Set<PossibleMove>): Set<PossibleMove> {
        return filterMoves(pieceMoveCalculator, color, moves)
    }

    private fun filterMoves(
        moveCalculator: PieceMoveCalculator,
        movingColor: Color,
        possibleMoves: Set<PossibleMove>,
    ): Set<PossibleMove> {
        return possibleMoves.filter { !checkGuard.isKingCheckedAfterMove(moveCalculator, movingColor, it) }.toSet()
    }

    // // todo do not need to filter if its a finishing move
    //  (implementing check mate should force to do it
}
