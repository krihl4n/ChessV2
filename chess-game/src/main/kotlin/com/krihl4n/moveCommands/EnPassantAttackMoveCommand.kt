package com.krihl4n.moveCommands

import com.krihl4n.MoveLabelGenerator
import com.krihl4n.PositionTracker
import com.krihl4n.model.*

internal class EnPassantAttackMoveCommand(
    private val move: Move,
    private val positionTracker: PositionTracker
) : MoveCommand {

    override fun execute() {
        positionTracker.removePieceFromField(findAttackedPawnLocation())
        positionTracker.movePiece(move.from, move.to)
    }

    override fun undo() {
        positionTracker.setPieceAtField(Piece(move.piece.color.opposite(), Type.PAWN), findAttackedPawnLocation())
        positionTracker.movePiece(move.to, move.from)
    }

    override fun getMove(): Move {
        return this.move
    }

    override fun getPiecePositionUpdate(): PiecePositionUpdate {
        return PiecePositionUpdate(
            primaryMove = move,
            pieceCapture = PieceCapture(
                field = findAttackedPawnLocation(),
                piece = Piece(move.piece.color.opposite(), Type.PAWN)
            ),
            recordedMove = MoveLabelGenerator.getLabel(move)
        )
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
