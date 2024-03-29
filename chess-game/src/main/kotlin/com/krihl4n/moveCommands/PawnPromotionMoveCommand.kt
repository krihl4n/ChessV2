package com.krihl4n.moveCommands

import com.krihl4n.CaptureTracker
import com.krihl4n.PositionTracker
import com.krihl4n.model.*
import com.krihl4n.model.Move
import com.krihl4n.model.Piece
import com.krihl4n.model.PieceCapture
import com.krihl4n.model.PiecePositionUpdate

internal class PawnPromotionMoveCommand(
    private val move: Move,
    private val positionTracker: PositionTracker,
    private val captureTracker: CaptureTracker,
    private val labelGenerator: MoveLabelGenerator
    ) : MoveCommand {

    private var killedPiece: Piece? = null

    private var update: PiecePositionUpdate? = null

    override fun execute() {
        if (this.move.pawnPromotion == null)
            throw IllegalArgumentException("No pawn promotion info")

        positionTracker.getPieceAt(move.to)?.let {
            captureTracker.pieceCaptured(it, move.to)
            killedPiece = it
        }
        positionTracker.removePieceFromField(move.from)
        positionTracker.setPieceAtField(Piece(move.piece.color, move.pawnPromotion), move.to)

        this.update = PiecePositionUpdate(
            primaryMove = move,
            pawnPromotion = move.pawnPromotion,
            pieceCapture = killedPiece?.let { PieceCapture(move.to, it) },
            label = labelGenerator.getLabel(move)
        )
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

    override fun getPiecePositionUpdate(): PiecePositionUpdate? {
        return update
    }
}
