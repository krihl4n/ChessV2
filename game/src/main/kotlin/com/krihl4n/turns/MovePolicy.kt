package com.krihl4n.turns

import com.krihl4n.model.Move

internal interface MovePolicy {

    fun moveAllowedBy(playerId: String, move: Move): Boolean
}