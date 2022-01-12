package com.krihl4n.command

import com.krihl4n.CaptureTracker
import com.krihl4n.PositionTracker
import com.krihl4n.model.Move
import com.krihl4n.model.Type

internal class CommandFactory(private val positionTracker: PositionTracker) {

    private val captureTracker = CaptureTracker()

    fun getCommand(move: Move): MoveCommand {
        if (kingAttemptsCastling(move))
            return CastlingMoveCommand(move, positionTracker)

        if (pawnReachesLastRank(move))
            return PawnToQueenMoveCommand(move, positionTracker, captureTracker)

        if (isEnPassantAttack(move))
            return EnPassantAttackMoveCommand(move, positionTracker)

        if (pieceAttacks(move))
            return StandardAttackMoveCommand(move, positionTracker, captureTracker)

        return StandardMoveCommand(move, positionTracker)
    }

    private fun isEnPassantAttack(move: Move) =
        move.piece.type == Type.PAWN && move.to.file != move.from.file && positionTracker.isFieldEmpty(move.to)

    private fun pieceAttacks(move: Move) = positionTracker.getPieceAt(move.to) != null

    private fun kingAttemptsCastling(move: Move) =
        move.piece.type == Type.KING && move.from.file.distanceTo(move.to.file) >= 2

    private fun pawnReachesLastRank(move: Move) =
        move.piece.type == Type.PAWN && move.to.rank.isLastFor(move.piece.color)
}
