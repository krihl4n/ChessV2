package com.krihl4n.moveCommands

import com.krihl4n.CaptureTracker
import com.krihl4n.PositionTracker
import com.krihl4n.model.*

internal class EnPassantAttackMoveCommand(
    private val move: Move,
    private val positionTracker: PositionTracker,
    private val captureTracker: CaptureTracker,
    private val labelGenerator: MoveLabelGenerator
) : MoveCommand {

    var update: PiecePositionUpdate? = null

    override fun execute() {
        captureTracker.pieceCaptured(Piece(move.piece.color.opposite(), Type.PAWN), findAttackedPawnLocation())
        positionTracker.removePieceFromField(findAttackedPawnLocation())
        positionTracker.movePiece(move.from, move.to)
        this.update = PiecePositionUpdate(
            primaryMove = move,
            pieceCapture = PieceCapture(
                field = findAttackedPawnLocation(),
                piece = Piece(move.piece.color.opposite(), Type.PAWN)
            ),
            label = labelGenerator.getLabel(move)
        )
    }

    override fun undo() {
        captureTracker.popLastPieceCapturedAtField(findAttackedPawnLocation())
        positionTracker.setPieceAtField(Piece(move.piece.color.opposite(), Type.PAWN), findAttackedPawnLocation())
        positionTracker.movePiece(move.to, move.from)
    }

    override fun getMove(): Move {
        return this.move
    }

    override fun getPiecePositionUpdate(): PiecePositionUpdate? {
        return update
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
