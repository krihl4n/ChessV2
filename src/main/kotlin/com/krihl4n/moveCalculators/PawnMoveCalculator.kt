package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.model.Color
import com.krihl4n.model.Field
import com.krihl4n.model.Piece
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
            val nextFieldForward = Field(start.file, Rank((start.rank.token.toInt() + 1).toString()))
            moves.add(PossibleMove(start, nextFieldForward))
        } else {
            val nextFieldForward = Field(start.file, Rank((start.rank.token.toInt() - 1).toString()))
            moves.add(PossibleMove(start, nextFieldForward))
        }

        return moves
    }

    private fun isLastRank(field: Field): Boolean {
        return field.rank == Rank("1") || field.rank == Rank("8")
    }
}
