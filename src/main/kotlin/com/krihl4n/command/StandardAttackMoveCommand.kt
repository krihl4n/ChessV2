package com.krihl4n.command

import com.krihl4n.Dependencies.Companion.captureTracker
import com.krihl4n.Dependencies.Companion.positionTracker
import com.krihl4n.model.Move

/*
    TODO maybe
    maybe this logic should be a part of the move entity, and we could have different types of moves
    and moves would send domain events, which inform about performed actions?
 */
class StandardAttackMoveCommand(
    private val move: Move,
) : MoveCommand {

    override fun execute() {
        val capturedPiece =
            positionTracker.getPieceAt(move.to) ?: throw IllegalStateException("no piece at desired field")
        if (capturedPiece.color == move.piece.color)
            throw IllegalArgumentException("cannot attack friendly pieces")
        captureTracker.pieceCaptured(capturedPiece, move.to)
        positionTracker.movePiece(move.from, move.to)
    }

    override fun undo() {
        val capturedPiece = captureTracker.popLastPieceCapturedAtField(move.to)
        val movingPiece =
            positionTracker.getPieceAt(move.to) ?: throw IllegalStateException("no piece at desired field")

        positionTracker.setPieceAtField(movingPiece, move.from)
        positionTracker.setPieceAtField(capturedPiece, move.to)
    }

    override fun getMove(): Move {
        return this.move
    }
}
