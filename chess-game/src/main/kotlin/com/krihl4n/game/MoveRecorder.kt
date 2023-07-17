package com.krihl4n.game

import com.krihl4n.model.PiecePositionUpdate
import com.krihl4n.moveCommands.PiecePositionUpdateListener

internal class MoveRecorder : PiecePositionUpdateListener {

    private val moves = ArrayDeque<String>()

    override fun positionsUpdated(update: PiecePositionUpdate) {
        if (!update.reverted) {
            moves.add(update.recordedMove)
        } else {
            moves.removeLast()
        }
    }

    fun getMoves(): List<String> {
        return moves.toList()
    }
}