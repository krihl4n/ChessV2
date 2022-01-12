package com.krihl4n.command

import com.krihl4n.PositionTracker
import com.krihl4n.model.Field
import com.krihl4n.model.Move

internal class CastlingMoveCommand(
    private val move: Move,
    private val positionTracker: PositionTracker
) : MoveCommand {

    override fun execute() {
        positionTracker.movePiece(move.from, move.to)

        val pair = determineRookMove()
        val rookFrom = pair.first
        val rookTo = pair.second

        positionTracker.movePiece(rookFrom, rookTo)
    }

    override fun undo() {
        positionTracker.movePiece(move.to, move.from)

        val pair = determineRookMove()
        val rookFrom = pair.first
        val rookTo = pair.second

        positionTracker.movePiece(rookTo, rookFrom)
    }

    private fun determineRookMove(): Pair<Field, Field> {
        val from: Field
        val to: Field
        if (move.from == Field("e1") && move.to == Field("c1")) {
            from = Field("a1")
            to = Field("d1")
        } else if (move.from == Field("e1") && move.to == Field("g1")) {
            from = Field("h1")
            to = Field("f1")
        } else if (move.from == Field("e8") && move.to == Field("c8")) {
            from = Field("a8")
            to = Field("d8")
        } else if (move.from == Field("e8") && move.to == Field("g8")) {
            from = Field("h8")
            to = Field("f8")
        } else {
            throw IllegalStateException("Cannot resolve castling")
        }
        return Pair(from, to)
    }

    override fun getMove(): Move {
        return this.move
    }
}
