package com.krihl4n.command

import com.krihl4n.Dependencies.Companion.positionTracker
import com.krihl4n.model.Color
import com.krihl4n.model.Field
import com.krihl4n.model.Move
import com.krihl4n.model.Rank

class EnPassantAttackMoveCommand(
    private val move: Move,
) : MoveCommand {

    override fun execute() {
        positionTracker.removePieceFromField(findAttackedPawnLocation())
        positionTracker.movePiece(move.from, move.to)

//        val capturedPiece =
//            positionTracker.getPieceAt(move.to) ?: throw IllegalStateException("no piece at desired field")
//        if (capturedPiece.color == move.piece.color)
//            throw IllegalArgumentException("cannot attack friendly pieces")
//        captureTracker.pieceCaptured(capturedPiece, move.to)
//        positionTracker.movePiece(move.from, move.to)
    }

    override fun undo() {
//        val capturedPiece = captureTracker.popLastPieceCapturedAtField(move.to)
//        val movingPiece =
//            positionTracker.getPieceAt(move.to) ?: throw IllegalStateException("no piece at desired field")
//
//        positionTracker.setPieceAtField(movingPiece, move.from)
//        positionTracker.setPieceAtField(capturedPiece, move.to)
    }

    override fun getMove(): Move {
        return this.move
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
