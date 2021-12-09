package com.krihl4n.command

import com.krihl4n.Dependencies.Companion.positionTracker
import com.krihl4n.model.Move

class StandardMoveCommand(private val move: Move) : MoveCommand {

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
