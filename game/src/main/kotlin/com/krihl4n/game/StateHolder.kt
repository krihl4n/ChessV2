package com.krihl4n.game

import com.krihl4n.api.dto.GameModeDto

internal interface StateHolder {

    fun setState(state: State, gameMode: GameModeDto? = null)
}