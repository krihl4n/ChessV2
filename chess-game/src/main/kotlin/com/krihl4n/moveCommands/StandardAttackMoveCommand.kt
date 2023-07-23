package com.krihl4n.moveCommands

import com.krihl4n.CaptureTracker
import com.krihl4n.PositionTracker
import com.krihl4n.model.Move
import com.krihl4n.model.PieceCapture
import com.krihl4n.model.PiecePositionUpdate

internal class StandardAttackMoveCommand(
    private val move: Move,
    private val positionTracker: PositionTracker,
    private val captureTracker: CaptureTracker,
    private val labelGenerator: MoveLabelGenerator
) : MoveCommand {

    var update: PiecePositionUpdate? = null

    override fun execute() {
        val capturedPiece =
            positionTracker.getPieceAt(move.to) ?: throw IllegalStateException("no piece at desired field")
        if (capturedPiece.color == move.piece.color)
            throw IllegalArgumentException("cannot attack friendly pieces")
        captureTracker.pieceCaptured(capturedPiece, move.to)
        positionTracker.movePiece(move.from, move.to)

        update = PiecePositionUpdate(
            primaryMove = move,
            pieceCapture = PieceCapture(move.to, capturedPiece),
            recordedMove = labelGenerator.getLabel(move)
        )
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

    override fun getPiecePositionUpdate(): PiecePositionUpdate? {
        return update
    }
}
