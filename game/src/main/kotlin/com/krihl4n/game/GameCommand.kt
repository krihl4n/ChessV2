package com.krihl4n.game

interface GameCommand {

    fun setState(state: State)

    fun executeStart(gameMode: GameMode)

    fun executeRegisterPlayer(playerId: String, colorPreference: String?): Boolean

    fun executeFinish()

    fun executePerformMove(playerId: String, from: String, to: String)

    fun executeUndo()

    fun executeRedo()
}
