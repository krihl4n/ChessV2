package com.krihl4n.turns

import com.krihl4n.command.MoveObserver
import com.krihl4n.model.Color
import com.krihl4n.model.Move

internal class ActualGameMovePolicy: MovePolicy, MoveObserver {

    private var colorAllowedToMove = Color.WHITE

    override fun moveAllowedBy(color: Color): Boolean {
        return color == colorAllowedToMove
    }

    override fun movePerformed(move: Move) {
        this.colorAllowedToMove = move.piece.color.opposite()
    }

    override fun moveUndid(move: Move) {
        this.colorAllowedToMove = move.piece.color
    }
}