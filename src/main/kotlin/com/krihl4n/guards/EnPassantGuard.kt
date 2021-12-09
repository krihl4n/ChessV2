package com.krihl4n.guards

import com.krihl4n.Dependencies.Companion.commandCoordinator
import com.krihl4n.Dependencies.Companion.positionTracker
import com.krihl4n.model.*
import com.krihl4n.moveCalculators.PossibleMove

class EnPassantGuard {

    fun getEnPassantMoves(): List<PossibleMove> {
        val lastMove = commandCoordinator.getLastMove() ?: return emptyList()
        if (lastMove.piece.type != Type.PAWN || lastMove.to.rank.distanceTo(lastMove.from.rank) < 2)
            return emptyList()

        val moves = mutableListOf<PossibleMove>()

        val pawnToTheLeft = getPawnAt(lastMove.to.file - 1, lastMove.to.rank)
        val pawnToTheRight = getPawnAt(lastMove.to.file + 1, lastMove.to.rank)

        val attackedField: Field = if (lastMove.piece.color == Color.BLACK) {
            Field(
                lastMove.to.file,
                (lastMove.to.rank + 1) ?: throw IllegalArgumentException("Illegal field"))
        } else {
            Field(
                lastMove.to.file,
                (lastMove.to.rank - 1) ?: throw IllegalArgumentException("Illegal field"))
        }

        pawnToTheLeft?.let {
            moves.add(
                PossibleMove(
                    Field((lastMove.to.file - 1)!!, lastMove.to.rank), attackedField))
        }

        pawnToTheRight?.let {
            moves.add(
                PossibleMove(
                    Field((lastMove.to.file + 1)!!, lastMove.to.rank), attackedField))
        }

        return moves
    }

    private fun getPawnAt(file: File?, rank: Rank): Piece? {
        file?.let {
            positionTracker.getPieceAt(Field(file, rank))?.let {
                if (it.type == Type.PAWN) {
                    return it
                }
            }
        }
        return null
    }
}