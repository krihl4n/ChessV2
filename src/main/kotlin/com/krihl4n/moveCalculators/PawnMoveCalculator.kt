package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.model.Color
import com.krihl4n.model.Field
import com.krihl4n.model.Rank

class PawnMoveCalculator(private val positionTracker: PositionTracker) : MoveCalculator {

    override fun calculateMoves(from: Field): Set<PossibleMove> {
        val moves = HashSet<PossibleMove>()
        val pawn = positionTracker.getPieceAt(from) ?: throw IllegalArgumentException("no piece at $from")

        if (from.rank.isLast()) {
            return moves
        }

        moves.add(PossibleMove(from, Field(from.file, from.rank.next(pawn.color))))
        if (from.isStartingPosition(pawn.color))
            moves.add(PossibleMove(from, Field(from.file, from.rank.next(pawn.color, 2))))

        return moves
    }

    private fun Rank.isLast(): Boolean {
        return this == Rank("1") || this == Rank("8")
    }

    private fun Rank.next(color: Color, steps: Int = 1): Rank {
        return if (color == Color.WHITE)
            this + steps
        else
            this - steps
    }

    private fun Field.isStartingPosition(color: Color): Boolean {
        return if (color == Color.BLACK)
            this.rank == Rank("7")
        else
            this.rank == Rank("2")
    }
}
