package com.krihl4n.moveCalculators

import com.krihl4n.PositionTracker
import com.krihl4n.guards.EnPassantGuard
import com.krihl4n.model.Color
import com.krihl4n.model.Field
import com.krihl4n.model.File
import com.krihl4n.model.Rank

internal class PawnMoveCalculator(private val enPassantGuard: EnPassantGuard) : MoveCalculator {

    override fun calculateMoves(from: Field, positionTracker: PositionTracker): Set<PossibleMove> {
        val moves = HashSet<PossibleMove>()
        val pawn = positionTracker.getPieceAt(from) ?: throw IllegalArgumentException("no piece at $from")

        if (from.rank.isLast()) { // todo for now this guarantees next rank null safety; refactor
            return moves
        }

        val nextFieldForward = Field(from.file, from.rank.next(pawn.color)!!)
        if (positionTracker.isFieldEmpty(nextFieldForward)) {
            moves.add(PossibleMove(from, nextFieldForward))
            if (from.isStartingPosition(pawn.color)) {
                val twoFieldsForward = Field(from.file, from.rank.next(pawn.color, 2)!!)
                if (positionTracker.isFieldEmpty(twoFieldsForward))
                    moves.add(PossibleMove(from, twoFieldsForward))
            }
        }

        from.file.left(pawn.color)
            ?.let {
                val attackField = Field(it, from.rank.next(pawn.color)!!)
                if (!positionTracker.isFieldEmpty(attackField) && positionTracker.getPieceAt(attackField)?.color != pawn.color)
                    moves.add(PossibleMove(from, attackField))
            }

        from.file.right(pawn.color)
            ?.let {
                val attackField = Field(it, from.rank.next(pawn.color)!!)
                if (!positionTracker.isFieldEmpty(attackField) && positionTracker.getPieceAt(attackField)?.color != pawn.color)
                    moves.add(PossibleMove(from, attackField))
            }

        moves.addAll(enPassantGuard.getEnPassantMoves(from))
        return moves
    }

    private fun Rank.isLast(): Boolean {
        return this == Rank("1") || this == Rank("8")
    }

    private fun Rank.next(color: Color, steps: Int = 1): Rank? {
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

    private fun File.left(color: Color): File? {
        return if (color == Color.WHITE)
            this - 1
        else
            this + 1
    }

    private fun File.right(color: Color): File? {
        return if (color == Color.WHITE)
            this + 1
        else
            this - 1
    }
}
