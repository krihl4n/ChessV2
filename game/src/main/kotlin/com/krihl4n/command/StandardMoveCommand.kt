package com.krihl4n.command

import com.krihl4n.PositionTracker
import com.krihl4n.api.MoveDto
import com.krihl4n.api.PiecePositionUpdateDto
import com.krihl4n.model.Move

internal class StandardMoveCommand(
    private val move: Move,
    private val positionTracker: PositionTracker
    ) : MoveCommand {

    private var update: PiecePositionUpdateDto? = null

    override fun execute() {
        positionTracker.movePiece(move.from, move.to)
        update = PiecePositionUpdateDto(    // todo domain should not be aware of dtos?
            MoveDto(move.from.token(), move.to.token())
        )
    }

    override fun undo() {
        positionTracker.movePiece(move.to, move.from)
    }

    override fun getMove(): Move {
        return this.move
    }

    override fun getPiecePositionUpdate(): PiecePositionUpdateDto {
        return update as PiecePositionUpdateDto
    }
}
