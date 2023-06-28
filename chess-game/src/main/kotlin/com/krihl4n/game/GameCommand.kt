package com.krihl4n.game

import com.krihl4n.api.dto.MoveDto
import com.krihl4n.model.Color
import com.krihl4n.players.Player

internal interface GameCommand {

    fun executeInitNewGame(gameMode: GameMode = GameMode.TEST_MODE)

    fun executePlayerReady(playerId: String, colorPreference: String?): Boolean

    fun executePerformMove(move: MoveDto)

    fun executeUndo(playerId: String)

    fun executeResign(playerId: String)

    fun fetchPlayer(playerId: String): Player? // todo separate query from commands?

    fun fetchPlayerOne(): Player

    fun fetchPlayerTwo(): Player

    fun fetchColorAllowedToMove(): Color
}
