package com.krihl4n.turns

import com.krihl4n.model.Move

internal class FreeMovePolicy: MovePolicy {

    override fun moveAllowedBy(playerId: String, move: Move): Boolean {
        return true
    }
}