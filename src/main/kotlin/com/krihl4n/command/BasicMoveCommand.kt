package com.krihl4n.command

import com.krihl4n.PositionTracker
import com.krihl4n.model.Move

class BasicMoveCommand(val move: Move, val positionTracker: PositionTracker) : MoveCommand {

    override fun execute() {
        positionTracker.movePiece(move.from, move.to)
    }

    override fun undo() {
        positionTracker.movePiece(move.to, move.from)
    }
}
