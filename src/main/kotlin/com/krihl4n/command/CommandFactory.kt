package com.krihl4n.command

import com.krihl4n.PositionTracker
import com.krihl4n.model.Move

class CommandFactory(val positionTracker: PositionTracker) {

    fun getCommand(move: Move): MoveCommand {

        positionTracker.getPieceAt(move.to)?.let {
            return AttackMoveCommand(move, positionTracker)
        }

        return BasicMoveCommand(move, positionTracker)
    }
}
