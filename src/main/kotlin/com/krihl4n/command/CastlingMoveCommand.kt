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
        if (move.from == Field("e1") && move.to == Field("c1")) {
            positionTracker.movePiece(Field("a1"), Field("d1"))
        } else if (move.from == Field("e1") && move.to == Field("g1")) {
            positionTracker.movePiece(Field("h1"), Field("f1"))
        } else if (move.from == Field("e8") && move.to == Field("c8")) {
            positionTracker.movePiece(Field("a8"), Field("d8"))
        } else if (move.from == Field("e8") && move.to == Field("g8")) {
            positionTracker.movePiece(Field("h8"), Field("f8"))
        }
    }

    override fun undo() {
    }
}
