package com.krihl4n.players

import com.krihl4n.model.Color

internal interface PlayersManager {

    fun registerPlayer(playerId: String, colorPreference: Color?): Boolean
    fun getPlayer(id: String): Player?
    fun getPlayerOne(): Player
    fun getPlayerTwo(): Player
}