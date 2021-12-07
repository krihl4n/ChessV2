package com.krihl4n.command

import com.krihl4n.Dependencies.Companion.positionTracker
import com.krihl4n.model.Move
import com.krihl4n.model.Type

class CommandFactory {

    fun getCommand(move: Move): MoveCommand {

        if (move.piece.type == Type.KING && move.from.file.distanceTo(move.to.file) >= 2)
            return CastlingMoveCommand(move)

        positionTracker.getPieceAt(move.to)?.let {
            return AttackMoveCommand(move)
        }

        return BasicMoveCommand(move)
    }
}

// en passant
// pawn reaches the end