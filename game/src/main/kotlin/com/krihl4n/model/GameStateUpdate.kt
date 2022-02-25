package com.krihl4n.model

class GameStateUpdate(
    val gameState: String, // todo separate class?
    val updateReason: StateUpdateReason = StateUpdateReason.CHECK_MATE // todo
)

enum class StateUpdateReason {
    CHECK_MATE
}