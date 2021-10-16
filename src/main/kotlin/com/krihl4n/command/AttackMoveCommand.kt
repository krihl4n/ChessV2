package com.krihl4n.command

import com.krihl4n.CaptureTracker
import com.krihl4n.PositionTracker
import com.krihl4n.model.Move

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
