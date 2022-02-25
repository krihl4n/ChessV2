package com.krihl4n.game

import com.krihl4n.model.GameStateUpdate

interface GameStateUpdateListener {

    fun gameStateUpdated(update: GameStateUpdate)
}