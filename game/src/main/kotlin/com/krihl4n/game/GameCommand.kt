package com.krihl4n.game

interface GameCommand {

    fun executeStart(gameMode: GameMode = GameMode.MOVE_FREELY)

    fun executeRegisterPlayer(playerId: String, colorPreference: String?): Boolean

    fun executeResign(playerId: String)

    fun executePerformMove(playerId: String, from: String, to: String)

    fun executeUndo()

    fun executeRedo()
}
