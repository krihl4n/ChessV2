package com.krihl4n.command

import com.krihl4n.Dependencies.Companion.captureTracker
import com.krihl4n.Dependencies.Companion.positionTracker
import com.krihl4n.model.Move
import com.krihl4n.model.Piece
import com.krihl4n.model.Type

class PawnToQueenMoveCommand(private val move: Move) : MoveCommand {

    override fun execute() {
        positionTracker.getPieceAt(move.to)?.let { captureTracker.pieceCaptured(it, move.to) }
        positionTracker.removePieceFromField(move.from)
        positionTracker.setPieceAtField(Piece(move.piece.color, Type.QUEEN), move.to)
    }

    override fun undo() {
        if(pawnAttacked()) {
            val capturedPiece = captureTracker.popLastPieceCapturedAtField(move.to)
            positionTracker.setPieceAtField(capturedPiece, move.to)
        } else {
            positionTracker.removePieceFromField(move.to)
        }

        positionTracker.setPieceAtField(Piece(move.piece.color, Type.PAWN), move.from)
    }

    private fun pawnAttacked() = move.to.file != move.from.file

    override fun getMove(): Move {
        return this.move
    }
}
