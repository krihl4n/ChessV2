package com.krihl4n.game

import com.krihl4n.model.GameStateUpdate

internal interface GameStateUpdateListener {

    fun gameStateUpdated(update: GameStateUpdate)
}