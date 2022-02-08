package com.krihl4n.turns

interface MovePolicy {

    fun moveAllowedBy(playerId: String): Boolean
}