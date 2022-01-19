package com.krihl4n.command

import com.krihl4n.CaptureTracker
import com.krihl4n.PositionTracker
import com.krihl4n.api.MoveDto
import com.krihl4n.api.PiecePositionUpdateDto
import com.krihl4n.model.Move
import com.krihl4n.model.Piece
import com.krihl4n.model.Type

internal class PawnToQueenMoveCommand(
    private val move: Move,
    private val positionTracker: PositionTracker,
    private val captureTracker: CaptureTracker
    ) : MoveCommand {

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

    override fun getPiecePositionUpdate(): PiecePositionUpdateDto {
        return PiecePositionUpdateDto(MoveDto(move.from.token(), move.to.token()))
    }
}
