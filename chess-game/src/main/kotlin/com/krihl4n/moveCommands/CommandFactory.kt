package com.krihl4n.moveCommands

import com.krihl4n.CaptureTracker
import com.krihl4n.PositionTracker
import com.krihl4n.model.Move
import com.krihl4n.model.Type

internal class CommandFactory(
    private val positionTracker: PositionTracker,
    private val labelGenerator: MoveLabelGenerator,
    private val captureTracker: CaptureTracker
) {

    fun getCommand(move: Move): MoveCommand {
        if (kingAttemptsCastling(move))
            return CastlingMoveCommand(move, positionTracker, labelGenerator)

        if (pawnReachesLastRank(move))
            return PawnPromotionMoveCommand(move, positionTracker, captureTracker, labelGenerator)

        if (isEnPassantAttack(move))
            return EnPassantAttackMoveCommand(move, positionTracker, labelGenerator)

        if (pieceAttacks(move))
            return StandardAttackMoveCommand(move, positionTracker, captureTracker, labelGenerator)

        return StandardMoveCommand(move, positionTracker, labelGenerator)
    }

    private fun isEnPassantAttack(move: Move) =
        move.piece.type == Type.PAWN && move.to.file != move.from.file && positionTracker.isFieldEmpty(move.to)

    private fun pieceAttacks(move: Move) = positionTracker.getPieceAt(move.to) != null

    private fun kingAttemptsCastling(move: Move) =
        move.piece.type == Type.KING && move.from.file.distanceTo(move.to.file) >= 2

    private fun pawnReachesLastRank(move: Move) =
        move.piece.type == Type.PAWN && move.to.rank.isLastFor(move.piece.color)
}
