package com.krihl4n.turns

class FreeMovePolicy: MovePolicy {

    override fun moveAllowedBy(playerId: String): Boolean {
        return true
    }
}