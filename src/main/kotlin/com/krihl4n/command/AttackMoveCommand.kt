package com.krihl4n.command

import com.krihl4n.CaptureTracker
import com.krihl4n.PositionTracker
import com.krihl4n.model.Move

/*
    TODO maybe
    maybe this logic should be a part of the move entity, and we could have different types of moves
    and moves would send domain events, which inform about performed actions?
 */
class AttackMoveCommand(
    val move: Move,
    private val positionTracker: PositionTracker,
    private val captureTracker: CaptureTracker
) : MoveCommand {

    override fun execute() {
        val capturedPiece =
            positionTracker.getPieceAt(move.to) ?: throw IllegalStateException("no piece at desired field")
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
}
