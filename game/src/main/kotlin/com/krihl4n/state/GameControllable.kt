package com.krihl4n.state

import com.krihl4n.GameMode

interface GameControllable {

    fun setState(state: State)

    fun executeStart(gameMode: GameMode)

    fun executeRegisterPlayer(playerId: String, colorPreference: String?): Boolean

    fun executeFinish()

    fun executePerformMove(playerId: String, from: String, to: String)

    fun executeUndo()

    fun executeRedo()
}
