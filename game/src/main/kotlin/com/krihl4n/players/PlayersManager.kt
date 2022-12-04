package com.krihl4n.players

import com.krihl4n.model.Color

interface PlayersManager {

    fun registerPlayer(id: String, colorPreference: Color?): Boolean
    fun getPlayer(id: String): Player?
    fun getPlayerOne(): Player
    fun getPlayerTwo(): Player
}