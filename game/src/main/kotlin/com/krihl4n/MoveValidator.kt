package com.krihl4n
import com.krihl4n.guards.CheckGuard
import com.krihl4n.model.Color
import com.krihl4n.model.Move
import com.krihl4n.moveCalculators.PieceMoveCalculator
import com.krihl4n.moveCalculators.PossibleMove
import com.krihl4n.moveCalculators.filters.OwnKingCannotBeCheckedAfterMoveFilter

internal class MoveValidator(private val pieceMoveCalculator: PieceMoveCalculator, checkGuard: CheckGuard) {

    private val filter = OwnKingCannotBeCheckedAfterMoveFilter(checkGuard)

    fun isMoveValid(move: Move) : Boolean {
        val possibleMoves = pieceMoveCalculator.findMoves(move.from)
        val filteredMoves = filterMoves(move.piece.color, possibleMoves)

        return filteredMoves.stream().anyMatch { it.from == move.from && it.to == move.to }
    }

    private fun filterMoves(color: Color, moves: Set<PossibleMove>): Set<PossibleMove> {
        return filter.filterMoves(pieceMoveCalculator, color, moves)
    }

    // // todo do not need to filter if its a finishing move
    //  (implementing check mate should force to do it
}
