package com.krihl4n.command

import com.krihl4n.CaptureTracker
import com.krihl4n.PositionTracker
import com.krihl4n.model.Move

class CommandFactory(private val positionTracker: PositionTracker, private val captureTracker: CaptureTracker) {

    fun getCommand(move: Move): MoveCommand {

        positionTracker.getPieceAt(move.to)?.let {
            return AttackMoveCommand(move, positionTracker, captureTracker)
        }

        return BasicMoveCommand(move, positionTracker)
    }
}
