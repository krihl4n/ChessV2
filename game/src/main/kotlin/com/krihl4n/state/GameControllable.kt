package com.krihl4n.state;

import com.krihl4n.GameMode;

interface GameControllable {

    fun setState(state: State)

    fun executeStart(gameMode: GameMode)

    fun executeFinish()

    fun executePerformMove(from: String, to: String)

    fun executeUndo()

    fun executeRedo()
}
