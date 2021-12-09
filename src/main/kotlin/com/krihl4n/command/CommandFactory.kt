package com.krihl4n.command

import com.krihl4n.Dependencies.Companion.positionTracker
import com.krihl4n.model.Move
import com.krihl4n.model.Type

class CommandFactory {

    fun getCommand(move: Move): MoveCommand {

        if (kingAttemptsCastling(move))
            return CastlingMoveCommand(move)

        if (pawnReachesLastRank(move)) {
             return PawnToQueenMoveCommand(move)
        }

        if (pieceAttacks(move)) {
            return AttackMoveCommand(move)
        }

        return BasicMoveCommand(move)
    }

    private fun pieceAttacks(move: Move): Boolean {
        return positionTracker.getPieceAt(move.to) != null
    }

    private fun kingAttemptsCastling(move: Move) =
        move.piece.type == Type.KING && move.from.file.distanceTo(move.to.file) >= 2

    private fun pawnReachesLastRank(move: Move) =
        move.piece.type == Type.PAWN && move.to.rank.isLastFor(move.piece.color)
}

// en passant