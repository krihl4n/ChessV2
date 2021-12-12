package com.krihl4n.command

import com.krihl4n.Dependencies.Companion.positionTracker
import com.krihl4n.model.*

class EnPassantAttackMoveCommand(
    private val move: Move,
) : MoveCommand {

    override fun execute() {
        positionTracker.removePieceFromField(findAttackedPawnLocation())
        positionTracker.movePiece(move.from, move.to)
    }

    override fun undo() {
        positionTracker.setPieceAtField(Piece(move.piece.color.opposite(), Type.PAWN), findAttackedPawnLocation())
        positionTracker.movePiece(move.to, move.from)
    }

    override fun getMove(): Move {
        return this.move
    }

    private fun Color.opposite(): Color {
        return if (this == Color.WHITE) Color.BLACK else Color.WHITE
    }

    private fun findAttackedPawnLocation(): Field {
        val rank: Rank? = if (move.piece.color == Color.WHITE) {
            move.to.rank - 1
        } else {
            move.to.rank + 1
        }

        return Field(move.to.file,
            rank ?: throw IllegalArgumentException("Cannot do en passant on field ${move.to}"))
    }
}
