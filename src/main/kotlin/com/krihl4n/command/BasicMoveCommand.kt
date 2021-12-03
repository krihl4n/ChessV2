package com.krihl4n.command

import com.krihl4n.PositionTracker
import com.krihl4n.model.Move

class BasicMoveCommand(private val move: Move, val positionTracker: PositionTracker) : MoveCommand {

    override fun execute() {
        positionTracker.movePiece(move.from, move.to)
    }

    override fun undo() {
        positionTracker.movePiece(move.to, move.from)
    }

    override fun getMove(): Move {
        return this.move
    }
}
