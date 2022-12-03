package com.krihl4n.game

import com.krihl4n.players.Player

interface GameCommand {

    fun executeStart(gameMode: GameMode = GameMode.MOVE_FREELY)

    fun executeRegisterPlayer(playerId: String, colorPreference: String?): Boolean

    fun fetchPlayer(playerId: String): Player? // todo separate query from commands?

    fun executeResign(playerId: String)

    fun executePerformMove(playerId: String, from: String, to: String)

    fun executeUndo()

    fun executeRedo()
}
