package com.krihl4n.model

import com.krihl4n.game.GameMode
import com.krihl4n.game.State

internal class GameStateUpdate(
    val gameState: State, // todo separate class?
    val updateReason: StateUpdateReason = StateUpdateReason.CHECK_MATE,
    val gameMode: GameMode
)

enum class StateUpdateReason {
    CHECK_MATE
}