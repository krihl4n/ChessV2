package com.krihl4n.moveCommands

import com.krihl4n.PositionTracker
import com.krihl4n.model.Move
import com.krihl4n.model.PiecePositionUpdate

internal class StandardMoveCommand(
    private val move: Move,
    private val positionTracker: PositionTracker,
    private val labelGenerator: MoveLabelGenerator
) : MoveCommand {

    var update: PiecePositionUpdate? = null

    override fun execute() {
        positionTracker.movePiece(move.from, move.to)
        this.update = PiecePositionUpdate(move, label = labelGenerator.getLabel(move))
    }

    override fun undo() {
        positionTracker.movePiece(move.to, move.from)
    }

    override fun getMove(): Move {
        return this.move
    }

    override fun getPiecePositionUpdate(): PiecePositionUpdate? {
        return update
    }
}
