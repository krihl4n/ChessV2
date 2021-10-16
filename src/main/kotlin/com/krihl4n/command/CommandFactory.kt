package com.krihl4n.command

import com.krihl4n.PositionTracker
import com.krihl4n.model.Move

class CommandFactory(val positionTracker: PositionTracker) {

    fun getCommand(move: Move): MoveCommand {
        val movingPiece = move.piece
        positionTracker.getPieceAt(move.to)?.let {
            if (it.color == movingPiece.color) {
                throw IllegalArgumentException("Field is occupied by same color piece")
            }
        }

        return BasicMoveCommand(move, positionTracker)
    }
}
