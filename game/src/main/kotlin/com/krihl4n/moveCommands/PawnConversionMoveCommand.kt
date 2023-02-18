package com.krihl4n.moveCommands

import com.krihl4n.CaptureTracker
import com.krihl4n.PositionTracker
import com.krihl4n.model.*
import com.krihl4n.model.Move
import com.krihl4n.model.Piece
import com.krihl4n.model.PieceCapture
import com.krihl4n.model.PiecePositionUpdate

internal class PawnConversionMoveCommand(
    private val move: Move,
    private val positionTracker: PositionTracker,
    private val captureTracker: CaptureTracker
    ) : MoveCommand {

    private var killedPiece: Piece? = null

    override fun execute() {
        if (this.move.conversion == null)
            throw IllegalArgumentException("No pawn conversion info")

        positionTracker.getPieceAt(move.to)?.let {
            captureTracker.pieceCaptured(it, move.to)
            killedPiece = it
        }
        positionTracker.removePieceFromField(move.from)
        positionTracker.setPieceAtField(Piece(move.piece.color, move.conversion), move.to)
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

    override fun getPiecePositionUpdate(): PiecePositionUpdate {
        return PiecePositionUpdate(
            primaryMove = move,
            pawnConversion = move.conversion,
            pieceCapture = killedPiece?.let { PieceCapture(move.to, it) }
        )
    }
}
