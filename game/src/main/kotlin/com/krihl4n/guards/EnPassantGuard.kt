package com.krihl4n.guards

import com.krihl4n.PositionTracker
import com.krihl4n.moveCommands.CommandCoordinator
import com.krihl4n.model.*
import com.krihl4n.moveCalculators.PossibleMove

internal class EnPassantGuard(
    private val positionTracker: PositionTracker,
    private val commandCoordinator: CommandCoordinator
) {

    fun getEnPassantMoves(): List<PossibleMove> {
        val lastMove = commandCoordinator.getLastMove() ?: return emptyList()
        if (lastMove.piece.type != Type.PAWN || lastMove.to.rank.distanceTo(lastMove.from.rank) < 2)
            return emptyList()

        val moves = mutableListOf<PossibleMove>()
        val attackedField: Field = findAttackedField(lastMove)

        getPawnToTheLeft(lastMove)?.let {
            moves.appendWith((lastMove.to.file - 1)!!, lastMove.to.rank, attackedField)
        }

        getPawnToTheRight(lastMove)?.let {
            moves.appendWith((lastMove.to.file + 1)!!, lastMove.to.rank, attackedField)
        }

        return moves
    }

    private fun MutableList<PossibleMove>.appendWith(fromFile: File, fromRank: Rank, toField: Field) {
        this.add(PossibleMove(Field(fromFile, fromRank), toField))
    }

    private fun getPawnToTheRight(lastMove: Move) = getPawnAt(lastMove.to.file + 1, lastMove.to.rank, lastMove.piece)

    private fun getPawnToTheLeft(lastMove: Move) = getPawnAt(lastMove.to.file - 1, lastMove.to.rank, lastMove.piece)

    private fun findAttackedField(lastMove: Move): Field {
        val rank: Rank? = if (lastMove.piece.color == Color.BLACK) {
            lastMove.to.rank + 1
        } else {
            lastMove.to.rank - 1
        }

        return Field(lastMove.to.file, rank ?: throw IllegalArgumentException("Illegal field"))
    }

    private fun getPawnAt(file: File?, rank: Rank, lastMovePawn: Piece): Piece? {
        file?.let {
            positionTracker.getPieceAt(Field(file, rank))?.let {
                if (it.type == Type.PAWN && it.color != lastMovePawn.color) {
                    return it
                }
            }
        }
        return null
    }
}
