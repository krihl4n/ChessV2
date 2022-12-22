package com.krihl4n.model

import com.krihl4n.api.dto.GameModeDto

class GameStateUpdate(
    val gameState: String, // todo separate class?
    val updateReason: StateUpdateReason = StateUpdateReason.CHECK_MATE,
    val gameMode: GameModeDto? = null
)

enum class StateUpdateReason {
    CHECK_MATE
}