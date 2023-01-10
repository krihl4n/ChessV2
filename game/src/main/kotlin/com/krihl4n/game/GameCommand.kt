package com.krihl4n.game

import com.krihl4n.api.dto.GameModeDto
import com.krihl4n.players.Player

interface GameCommand {

    fun executeInitNewGame(gameMode: GameModeDto = GameModeDto.TEST_MODE)

    fun executePlayerReady(colorPreference: String?): Boolean

    fun fetchPlayer(playerId: String): Player? // todo separate query from commands?

    fun fetchPlayerOne(): Player

    fun fetchPlayerTwo(): Player

    fun fetchGameMode(): GameModeDto?

    fun executeResign(playerId: String)

    fun executePerformMove(playerId: String, from: String, to: String)

    fun executeUndo()

    fun executeRedo()
}
