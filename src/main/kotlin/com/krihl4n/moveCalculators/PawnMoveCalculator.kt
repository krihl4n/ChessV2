package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.model.Color
import com.krihl4n.model.Field
import com.krihl4n.model.Rank
import kotlin.collections.HashSet

class PawnMoveCalculator(private val positionTracker: PositionTracker) : MoveCalculator {

    override fun calculateMoves(start: Field): Set<PossibleMove> {
        val moves = HashSet<PossibleMove>()
        val pawn = positionTracker.getPieceAt(start) ?: throw IllegalArgumentException("no piece at $start")

        if (isLastRank(start)) {
            return moves
        }

        if (pawn.color == Color.WHITE) {
            moves.add(PossibleMove(start, Field(start.file, start.rank + 1)))
            if (isWhiteStartingPosition(start))
                moves.add(PossibleMove(start, Field(start.file, start.rank + 2)))
        } else {
            moves.add(PossibleMove(start, Field(start.file, start.rank - 1)))
            if (isBlackStartingPosition(start))
                moves.add(PossibleMove(start, Field(start.file, start.rank - 2)))
        }

        return moves
    }

    private fun isLastRank(field: Field): Boolean {
        return field.rank == Rank("1") || field.rank == Rank("8")
    }

    private fun isWhiteStartingPosition(field: Field): Boolean {
        return field.rank == Rank("2")
    }

    private fun isBlackStartingPosition(field: Field): Boolean {
        return field.rank == Rank("7")
    }
}
