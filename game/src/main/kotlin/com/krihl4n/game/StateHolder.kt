package com.krihl4n.game

internal interface StateHolder {

    fun setState(state: State, gameMode: GameMode? = null)
}