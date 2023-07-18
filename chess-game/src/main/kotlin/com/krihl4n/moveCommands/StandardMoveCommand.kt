package com.krihl4n.moveCommands

import com.krihl4n.PositionTracker
import com.krihl4n.model.Move
import com.krihl4n.model.PiecePositionUpdate

internal class StandardMoveCommand(
    private val move: Move,
    private val positionTracker: PositionTracker
    ) : MoveCommand {

    override fun execute() {
        positionTracker.movePiece(move.from, move.to)
    }

    override fun undo() {
        positionTracker.movePiece(move.to, move.from)
    }

    override fun getMove(): Move {
        return this.move
    }

    override fun getPiecePositionUpdate(): PiecePositionUpdate {
        return PiecePositionUpdate(move, recordedMove = move.to.token())
    }
}
