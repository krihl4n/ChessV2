package com.krihl4n.command

import com.krihl4n.PositionTracker
import com.krihl4n.model.Field
import com.krihl4n.model.Move

class CastlingMoveCommand(
    val move: Move,
    private val positionTracker: PositionTracker
) : MoveCommand {

    override fun execute() {
        positionTracker.movePiece(move.from, move.to)
        positionTracker.movePiece(Field("a1"), Field("d1"))
    }

    override fun undo() {
    }
}
