package com.krihl4n.command

import com.krihl4n.CaptureTracker
import com.krihl4n.PositionTracker
import com.krihl4n.model.Move
import com.krihl4n.model.Type

class CommandFactory(private val positionTracker: PositionTracker, private val captureTracker: CaptureTracker) {

    fun getCommand(move: Move): MoveCommand {

        if(move.piece.type == Type.KING && move.from.file.distanceTo(move.to.file) >= 2)
            return CastlingMoveCommand(move, positionTracker)
        positionTracker.getPieceAt(move.to)?.let {
            return AttackMoveCommand(move, positionTracker, captureTracker)
        }

        return BasicMoveCommand(move, positionTracker)
    }
}

// castling
// en passant
// pawn reaches the end